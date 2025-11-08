package exercises.e4

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import labutils.Scenarios
import labutils.robot.RobotBehavior
import labutils.math.Point
import java.awt.Color
import labutils.simulator.Simulator.Companion.actor
import exercises.e3.AsyncAwait.async
import exercises.e3.AsyncAwait.setInterval

fun main() {
    Scenarios.PreyPredator.runScenario(
        preyBehavior = Actors.preyBehavior,
        predatorBehavior = Actors.predatorBehavior,
        width = 600.0,
        height = 600.0,
    )
}

@OptIn(ObsoleteCoroutinesApi::class)
object Actors {
    // SCENARIO
    // The prey tries to escape from the predator by running in circles.
    // The prey emits a scent that is followed by the predator.
    // The predator will catch the prey if it gets close enough.

    // CHANNELS
    // Note: in Kotlin, actors uses channels to communicate and identify each other.
    // Channels can only accept the specified kind of `Message`.
    private enum class RobotId { Prey, Predator }
    private val channels: MutableMap<RobotId, SendChannel<Message>> = mutableMapOf()

    // MESSAGES
    // Note: in Kotlin, here is a way to create an Algebraic Data Type (ADT) (e.g. an `enum class` of Scala).
    // A `data class` is the same as a `case class` in Scala or `record` in Java.
    private sealed class Message
    private data class Scent(val position: Point) : Message()
    private data object PredatorCatching : Message()
    private data object PreyCaught : Message()

    // BEHAVIORS
    val preyBehavior: RobotBehavior = { robot ->
        // Note: you can combine multiple asynchronous techniques
        val escaping = setInterval(10) {
            val moving = async { robot.motor.forward(100.0) }
            val rotating = async { robot.spinMotor.rotate(180.0) }
        }
        val scent = setInterval(100) {
            // Note: here we send a `Scent` message with our position to the robot `Predator`
            channels[RobotId.Predator]?.send(Scent(robot.body.position))
        }
        // Note: `actor` creates a new actor with the specified behavior. It also
        // returns a `SendChannel` that can be used to send messages to the new actor.
        channels[RobotId.Prey] = actor {
            // Note: here we start listening to the channel indefinitely. In fact, the channel
            // is an infinite stream of messages. This is similar to `receive` in Erlang.
            for (msg in channel) {
                // Note: each received message is processed using a `switch-case`
                // (not as powerful as Scala pattern matching).
                when (msg) {
                    is PredatorCatching -> {
                        robot.led.switch(on = true, color = Color.RED)
                        channels[RobotId.Predator]?.send(PreyCaught)
                    }
                    else -> println("No behavior defined for message: $msg")
                }
            }
        }
    }

    val predatorBehavior: RobotBehavior = { robot ->
        var preyPosition = Point(0.0, 0.0)    // local state
        val pursueing = setInterval(10){
            val preyDirection = robot.body.relativeDirection(preyPosition)
            val preyDistance = preyDirection.length - 2 * robot.body.bodyRadius
            if (preyDistance < 5.0) {
                channels[RobotId.Prey]?.send(PredatorCatching)
            } else {
                val moving = async { robot.motor.forward(preyDirection.length) }
                val rotating = async { robot.spinMotor.rotate(preyDirection.angle) }
            }
        }
        channels[RobotId.Predator] = actor {
            for (msg in channel) {
                when (msg) {
                    is Scent -> { preyPosition = msg.position }
                    is PreyCaught -> robot.led.switch(on = true, color = Color.GREEN)
                    else -> println("No behavior defined for message: $msg")
                }
            }
        }
    }
}