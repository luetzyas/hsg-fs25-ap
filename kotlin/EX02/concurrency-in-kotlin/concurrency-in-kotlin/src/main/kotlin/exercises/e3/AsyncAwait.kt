package exercises.e3

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import labutils.Scenarios
import labutils.robot.RobotBehavior
import labutils.simulator.Simulator
import java.awt.Color

fun main() {
    Scenarios.OneRobotOneLight.runScenario(
        behavior = AsyncAwait.blinkingSOSAndFollowingTheLight,
        width = 600.0,
        height = 600.0,
    )
}

object AsyncAwait {
    // Note: `async` can be used to defer the execution of code some time later.
    // The exact time of execution is not known, but it is usually "as soon as possible".
    // In Kotlin, deferred executions are modelled by the class `Deferred`.
    fun <T> async(callback: suspend () -> T): Deferred<T> =
        Simulator.async { callback() }

    fun <T> setTimeout(durationMillis: Long, callback: suspend () -> T): Deferred<T> =
        Simulator.asyncAfter(durationMillis) { callback() }

    // Note: async/await can be used to write asynchronous code in sequence, flattening the callback hell
    fun blinkingSOS(): RobotBehavior = { robot ->
        setTimeout(100){ robot.led.switch(on = true, color = Color.RED) }.await()
        setTimeout(100){ robot.led.switch(on = false, color = Color.RED) }.await()
        setTimeout(100){ robot.led.switch(on = true, color = Color.RED) }.await()
        setTimeout(100){ robot.led.switch(on = false, color = Color.RED) }.await()
        setTimeout(100){ robot.led.switch(on = true, color = Color.RED) }.await()
        setTimeout(100){ robot.led.switch(on = false, color = Color.RED) }.await()
        setTimeout(200){ robot.led.switch(on = true, color = Color.GREEN) }.await()
        setTimeout(200){ robot.led.switch(on = false, color = Color.GREEN) }.await()
        setTimeout(200){ robot.led.switch(on = true, color = Color.GREEN) }.await()
        setTimeout(200){ robot.led.switch(on = false, color = Color.GREEN) }.await()
        setTimeout(200){ robot.led.switch(on = true, color = Color.GREEN) }.await()
        setTimeout(200){ robot.led.switch(on = false, color = Color.GREEN) }.await()
        setTimeout(100){ robot.led.switch(on = true, color = Color.RED) }.await()
        setTimeout(100){ robot.led.switch(on = false, color = Color.RED) }.await()
        setTimeout(100){ robot.led.switch(on = true, color = Color.RED) }.await()
        setTimeout(100){ robot.led.switch(on = false, color = Color.RED) }.await()
        setTimeout(100){ robot.led.switch(on = true, color = Color.RED) }.await()
        setTimeout(100){ robot.led.switch(on = false, color = Color.RED) }.await()
        setTimeout(1000){ blinkingSOS()(robot) }.await()
    }

    fun followingTheLight(): RobotBehavior = { robot ->
        setTimeout(10) {
            val closestLight = robot.lightSensor.closestLight()
            if (closestLight != null) {
                // Note: with `async` with can easily create smaller asynchronous jobs.
                // Now, the robot moves WHILE rotating (before it moved only after rotating)
                val rotating = async { robot.spinMotor.rotate(0.2 * closestLight.direction.angle) }
                val moving = async { robot.motor.forward(0.1 * closestLight.direction.length) }
            }
        }.await()
        followingTheLight()(robot)
    }

    val blinkingSOSAndFollowingTheLight: RobotBehavior = { robot ->
        val blinking = async { blinkingSOS()(robot) }
        val following = async { followingTheLight()(robot) }
        // blinking.cancel()  // Note: you can also cancel `Deferred` executions
    }

    // Note: since `Deferred` executions can be cancelled, we can define primitives for behaviors
    // that can be cancelled. Here is a `setInterval` that can be cancelled.
    fun setInterval(periodMillis: Long, callback: suspend () -> Unit): Deferred<Unit> {
        fun auxiliary(periodMillis: Long, setIntervalJob: Deferred<Unit>, callback: suspend () -> Unit) {
            if (setIntervalJob.isActive) {
                val i = setTimeout(periodMillis) {
                    callback()
                    auxiliary(periodMillis, setIntervalJob, callback)
                }
            }
        }
        val deferred = CompletableDeferred<Unit>()
        auxiliary(periodMillis, deferred, callback)
        return deferred
    }
}