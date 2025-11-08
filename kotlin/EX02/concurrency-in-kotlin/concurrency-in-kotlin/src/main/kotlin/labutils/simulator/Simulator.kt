package labutils.simulator

import labutils.environment.Environment
import labutils.gui.Observable
import labutils.robot.RobotBehavior
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ActorScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import labutils.math.Point
import labutils.robot.Robot
import java.lang.Math.toRadians
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.math.*

class Simulator(private val environment: Environment, clock: Observable<Tickable.Tick>) {
    init { clock.observe { runLater { tick(it) } } }

    fun schedule(robot: Robot, behavior: RobotBehavior): Simulator {
        runLater { robot.enter(behavior) }
        return this
    }
    private suspend fun tick(tick: Tickable.Tick) {
        this.environment.robots().forEach { it.tick(environment, tick) }
        Invariants.robotsNoOverlapping(this.environment)
        Invariants.robotsInsideEnvironment(this.environment)
    }

    private object Invariants {
        fun robotsNoOverlapping(environment: Environment) {
            val robots = environment.robots()
            robots.forEach { thisRobot ->
                robots.forEach { otherRobot ->
                    if (thisRobot != otherRobot) {
                        val collisionDirection = thisRobot.body.position.direction(otherRobot.body.position)
                        val collisionDistance = (thisRobot.body.bodyRadius + otherRobot.body.bodyRadius) - collisionDirection.length
                        if (collisionDistance > 0) {
                            thisRobot.body.position.x += (-collisionDistance * cos(toRadians(collisionDirection.angle)))
                            thisRobot.body.position.y += (collisionDistance * sin(toRadians(collisionDirection.angle)))
                            otherRobot.body.position.x += (collisionDistance * cos(toRadians(collisionDirection.angle)))
                            otherRobot.body.position.y += (-collisionDistance * sin(toRadians(collisionDirection.angle)))
                        }
                    }
                }
            }
        }
        fun robotsInsideEnvironment(environment: Environment) {
            environment.robots().forEach { robot ->
                val min = Point(robot.body.bodyRadius, robot.body.bodyRadius)
                val max = Point(environment.configuration.width - min.x, environment.configuration.height - min.y)
                robot.body.position.x = min(max(robot.body.position.x, min.x), max.x)
                robot.body.position.y = min(max(robot.body.position.y, min.y), max.y)
            }
        }
    }

    companion object {
        private val Executor: Executor = Executors.newSingleThreadExecutor()
        val CouroutineScope: CoroutineScope = CoroutineScope(Executor.asCoroutineDispatcher())

        fun <T> async(block: suspend CoroutineScope.() -> T): Deferred<T> =
            CouroutineScope.async(block = block)
        fun <T> asyncAfter(duration: Long, block: suspend CoroutineScope.() -> T): Deferred<T> =
            CouroutineScope.async { delay(duration); block() }
        fun <T> runLater(block: suspend CoroutineScope.() -> T) =
            async(block = block).invokeOnCompletion { it?.printStackTrace() }
        fun <T> runAfter(duration: Long, block: suspend CoroutineScope.() -> T) =
            runLater { delay(duration); block() }
        @OptIn(ObsoleteCoroutinesApi::class)
        fun <T> actor(block: suspend ActorScope<T>.() -> Unit): SendChannel<T> =
            CouroutineScope.actor { block() }
    }
}