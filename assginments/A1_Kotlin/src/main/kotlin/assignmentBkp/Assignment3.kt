package assignmentBkp

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import labutils.*
import labutils.math.Point
import labutils.robot.RobotBehavior
import labutils.simulator.Simulator
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.system.exitProcess

fun main() {
    Scenarios.Assignment.runScenario(
        redBehavior = Assignments3.redBehavior,
        greenBehavior = Assignments3.greenBehavior,
        blueBehavior = Assignments3.blueBehavior,
        width = 600.0,
        height = 600.0
    )
}

@OptIn(ObsoleteCoroutinesApi::class)
object Assignments3 {
    // SCENARIO
    // Three colored robots wants to reach the light of their own color:
    // - The red robot wants to reach the red light.
    // - The blue robot wants to reach the blue light.
    // - The green robot wants to reach the green light.
    // Their light sensors can only detect the closest light, which may
    // have a different color than their own. In fact, each robot starts
    // closest to a light with a different color:
    // - The red robot starts near the blue light
    // - The green robot starts near the red light
    // - the blue robot start near the green light
    // The robots must exchange messages to share local information about
    // the environment and achieved their own goals.
    // The robots should also exchange messages about their goals. In
    // particular, only after all three robots have reached their goal,
    // they all start blinking.

    // CHANNELS
    private val channels: MutableMap<Color, SendChannel<Message>> = mutableMapOf()
    // MESSAGES
    private sealed class Message
    private data class LightSeen(val color: Color, val position: Point) : Message()
    private data class TargetLightFound(val target: Color, val position: Point) : Message()
    private data class GoalReached(val color: Color) : Message()
    private data object Celebration : Message()

    private fun colorName(c: Color) =
        when (c) { Color.RED -> "RED"; Color.GREEN -> "GREEN"; Color.BLUE -> "BLUE"; else -> "?" }

    // ---------------------------------------------------------
    // BUILD BEHAVIOR (alle drei Roboter benutzen dieselbe Logik)
    // ---------------------------------------------------------
    private fun buildBehavior(currColor: Color, name: String): RobotBehavior = { robot ->
        // declared state variables
        var targetLight: Point? = null
        var reached = false
        var celebrating = false
        val knownLights = mutableMapOf<Color, Point>()
        val reachedMap = mutableMapOf(
            Color.RED to false,
            Color.GREEN to false,
            Color.BLUE to false
        )
        println("Hi I'm $name")
        channels[currColor] = Simulator.actor {
            for (msg in channel) {
                when (msg) {
                    is LightSeen -> {
                        knownLights[msg.color] = msg.position
                        println("$name saw ${colorName(msg.color)} at ${msg.position}")

                        if (msg.color == currColor) {
                            if (targetLight == null) {
                                targetLight = msg.position
                                println("$name stored its target light at $targetLight")
                            }
                        } else {
                            channels[msg.color]?.let { receiver ->
                                Simulator.async {
                                    receiver.send(TargetLightFound(msg.color, msg.position))
                                }
                            }
                        }
                    }
                    is TargetLightFound -> {
                        if (msg.target == currColor && targetLight == null) {
                            targetLight = msg.position
                            println("$name received its true target position $targetLight")
                        }
                    }
                    is GoalReached -> {
                        reachedMap[msg.color] = true
                        val allDone = reachedMap.values.all { it }
                        if (allDone && reached) {
                            channels.values.forEach { ch ->
                                Simulator.async { ch.send(Celebration) }
                            }
                        }
                    }
                    is Celebration -> {
                        celebrating = true
                        println("$name starts blinking")
                    }
                }
            }
        }
        // -----------------------------------------------------
        // Task 2: Coordination — absolute Lichtposition berechnen
        // -----------------------------------------------------
        setInterval(200) {
            Simulator.async {
                val closest = robot.lightSensor.closestLight()
                if (closest != null) {
                    // compute absolute position
                    val robotPos = robot.body.position
                    val dir = closest.direction
                    val pos = Point(
                        robotPos.x + dir.length * cos(dir.angle),
                        robotPos.y + dir.length * sin(dir.angle)
                    )
                    // send to correct actor
                    channels[closest.color]?.send(LightSeen(closest.color, pos))
                }
            }
        }
        // -----------------------------------------------------
        // Task 3: Reach the Light
        // -----------------------------------------------------
        setInterval(150) {
            Simulator.async {
                val target = targetLight
                if (target != null && !reached) {
                    val dir = robot.body.relativeDirection(target)
                    if (dir.length < 10) {
                        reached = true
                        println("$name reached its target!")
                        robot.led.switch(on = true, color = currColor)
                        channels.values.forEach { ch ->
                            ch.send(GoalReached(currColor))
                        }
                    } else {
                        robot.spinMotor.rotate(dir.angle)
                        robot.motor.forward(dir.length)
                    }
                }
            }
        }
        // -----------------------------------------------------
        // Task 4: Celebration
        // -----------------------------------------------------
        var ledOn = false
        setInterval(500) {
            if (celebrating) {
                Simulator.async {
                    ledOn = !ledOn
                    robot.led.switch(on = ledOn, color = currColor)
                }
            }
        }
    }
    // assingment behavior actors
    val redBehavior = buildBehavior(Color.RED, "Redly")
    val greenBehavior = buildBehavior(Color.GREEN, "GREENaldo")
    val blueBehavior = buildBehavior(Color.BLUE, "BLUEbert")
    // setTimeout
    private fun setTimeout(ms: Long, f: () -> Unit) {
        Simulator.runAfter(ms) { f() }
    }
    // setInterval
    private fun setInterval(ms: Long, f: () -> Unit) {
        setTimeout(ms) {
            f()
            setInterval(ms, f)
        }
    }
}
