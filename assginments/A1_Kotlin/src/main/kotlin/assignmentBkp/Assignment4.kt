package assignmentBkp

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.Channel
import labutils.*
import labutils.math.Point
import labutils.robot.RobotBehavior
import labutils.simulator.Simulator
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin

fun main() {
    Scenarios.Assignment.runScenario(
        redBehavior = Assignment4.redBehavior,
        greenBehavior = Assignment4.greenBehavior,
        blueBehavior = Assignment4.blueBehavior,
        width = 600.0,
        height = 600.0
    )
}

@OptIn(ObsoleteCoroutinesApi::class)
object Assignment4 {

    private val channels: MutableMap<Color, SendChannel<Message>> = mutableMapOf()

    private sealed class Message
    private data class LightSeen(val color: Color, val pos: Point) : Message()
    private data class GoalReached(val color: Color) : Message()
    private data object Celebrate : Message()

    private fun buildBehavior(robotColor: Color, name: String): RobotBehavior = { robot ->

        var targetPos: Point? = null
        var reached = false
        var celebration = false
        var targetLocked = false

        val others = mutableMapOf(
            Color.RED to false,
            Color.GREEN to false,
            Color.BLUE to false
        )

        println("Hi I am $name")

        channels[robotColor] = Simulator.actor {
            for (msg in channel) {
                when(msg) {
                    is LightSeen -> {
                        if (msg.color == robotColor) {
                            if (!targetLocked) {
                                targetPos = msg.pos
                                targetLocked = true
                                println("$name received its light at $targetPos")
                            }
                        }
                    }
                    is GoalReached -> {
                        others[msg.color] = true
                        if (others.values.all { it } && reached) {
                            channels.values.forEach { ch ->
                                Simulator.async { ch.send(Celebrate) }
                            }
                        }
                    }
                    is Celebrate -> {
                        celebration = true
                        println("$name starts dancing")
                        /*Simulator.runAfter(60000) {
                            println("Stopping after one minute of party")
                            Simulator.stop()
                        }*/
                    }
                    else -> println("No behavior for $name defined for message: $msg")
                }
            }
        }

        setInterval(200) {
            Simulator.async {
                if (targetLocked) return@async
                val sensed = robot.lightSensor.closestLight()
                if (sensed != null) {

                    val dir = sensed.direction
                    val posRobot = robot.body.position
                    val px = posRobot.x + dir.length * cos(dir.angle)
                    val py = posRobot.y + dir.length * sin(dir.angle)
                    val absolute = Point(px, py)

                    channels[sensed.color]?.send(
                        LightSeen(sensed.color, absolute)
                    )
                }
            }
        }

        setInterval(150) {
            Simulator.async {
                val t = targetPos
                if (!reached && t != null) {
                    val dir = robot.body.relativeDirection(t)
                    if (dir.length < 10.0) {
                        reached = true
                        robot.led.switch(on = true, color = robotColor)
                        println("$name reached its light")
                        channels.values.forEach { ch ->
                            ch.send(GoalReached(robotColor))
                        }
                    } else {
                        robot.spinMotor.rotate(dir.angle)
                        robot.motor.forward(dir.length)
                    }
                }
            }
        }

        var led = false
        setInterval(500) {
            if (celebration) {
                Simulator.async {
                    led = !led
                    robot.led.switch(on = led, color = robotColor)
                }
            }
        }
    }

    val redBehavior = buildBehavior(Color.RED, "Redly")
    val greenBehavior = buildBehavior(Color.GREEN, "Greenaldo")
    val blueBehavior = buildBehavior(Color.BLUE, "Bluebert")

    private fun setTimeout(ms: Long, f: () -> Unit) {
        Simulator.runAfter(ms) { f() }
    }

    private fun setInterval(ms: Long, f: () -> Unit) {
        setTimeout(ms) {
            f()
            setInterval(ms, f)
        }
    }
}
