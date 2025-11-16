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

fun main() {
    Scenarios.Assignment.runScenario(
        redBehavior = Assignment2.redBehavior,
        greenBehavior = Assignment2.greenBehavior,
        blueBehavior = Assignment2.blueBehavior,
        width = 600.0,
        height = 600.0
    )
}

@OptIn(ObsoleteCoroutinesApi::class)
object Assignment2 {
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

    // Channels
    private val channels: MutableMap<Color, SendChannel<Message>> = mutableMapOf()
    // Messages
    private sealed class Message
    private data class LightSeen(val color: Color, val position: Point) : Message()
    private data class TargetLightFound(val target: Color, val position: Point) : Message()
    private data class GoalReached(val color: Color) : Message()
    private data object Celebration : Message()
    // Helper for readable names
    private fun colorName(color: Color): String =
        when (color) {
            Color.RED -> "RED"
            Color.GREEN -> "GREEN"
            Color.BLUE -> "BLUE"
            else -> "UNKNOWN"
        }
    // Build one unified behavior for any robot color
    private fun buildBehavior(currColor: Color, name: String): RobotBehavior = { robot ->
        // State
        var targetLightPosition: Point? = null
        var reached = false
        var celebrating = false
        // Store known lights
        val knownLights = mutableMapOf<Color, Point>()
        val othersReached = mutableMapOf(
            Color.RED to false,
            Color.GREEN to false,
            Color.BLUE to false
        )
        // Actor Introduction ;D
        println("Hi I'm $name")
        // Actor for receiving messages
        channels[currColor] = Simulator.actor {
            for (msg in channel) {
                when (msg) {
                    is LightSeen -> {
                        knownLights[msg.color] = msg.position
                        println("$name saw ${colorName(msg.color)} at ${msg.position}")
                        // Ziel-Licht: Nur einmal setzen
                        if (msg.color == currColor) {
                            if (targetLightPosition == null) {
                                targetLightPosition = msg.position
                                println("$name: fixed its target light at $targetLightPosition")
                            } else {
                                println("$name: ignores new position for its own light")
                            }
                        } else {
                            // Weiterleiten an Besitzerfarbe
                            channels[msg.color]?.let { ch ->
                                Simulator.async {
                                    ch.send(TargetLightFound(msg.color, msg.position))
                                }
                            }
                        }
                    }
                    is TargetLightFound -> {
                        if (msg.target == currColor) {
                            if (targetLightPosition == null) {
                                targetLightPosition = msg.position
                                println("$name: received and fixed its target light at $targetLightPosition")
                            } else {
                                println("$name: ignores forwarded targetLight because it already has one")
                            }
                        }
                    }
                    is GoalReached -> {
                        othersReached[msg.color] = true
                        println("$name: learned that ${colorName(msg.color)} reached goal")

                        if (othersReached.values.all { it } && reached) {
                            channels.values.forEach { ch ->
                                Simulator.async { ch.send(Celebration) }
                            }
                        }
                    }

                    is Celebration -> {
                        println("$name: starts celebrating")
                        celebrating = true
                    }
                }
            }
        }
        // Task 2: Coordination
        setInterval(200) {
            Simulator.async {
                val closest = robot.lightSensor.closestLight()
                if (closest != null) {
                    // Absolute position des Lichts korrekt berechnen:
                    val dir = closest.direction
                    val robotPos = robot.body.position
                    val pos = Point(
                        robotPos.x + dir.length * kotlin.math.cos(dir.angle),
                        robotPos.y + dir.length * kotlin.math.sin(dir.angle)
                    )
                    // Nachricht nur an den Roboter der Farbe senden
                    channels[closest.color]?.send(
                        LightSeen(color = closest.color, position = pos)
                    )
                }
            }
        }
        // Task 3: Reach the Light
        setInterval(150) {
            Simulator.async {
                val target = targetLightPosition
                if (target != null && !reached) {
                    val dir = robot.body.relativeDirection(target)
                    // Prüfen ob nah genug
                    if (dir.length < 10) {
                        reached = true
                        println("$name: reached its target light")
                        // An alle broadcasten
                        channels.values.forEach { ch ->
                            ch.send(GoalReached(currColor))
                        }
                        // LED einschalten
                        robot.led.switch(on = true, color = currColor)
                    } else {
                        // Normale Bewegung
                        robot.spinMotor.rotate(dir.angle)
                        robot.motor.forward(dir.length)
                    }
                }
            }
        }
        // Task 4: Celebration
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
    // Assign behaviors
    val redBehavior = buildBehavior(Color.RED, "Redly")
    val greenBehavior = buildBehavior(Color.GREEN, "GREENaldo")
    val blueBehavior = buildBehavior(Color.BLUE, "BLUEbert")
    // setTimeout
    private fun setTimeout(durationMillis: Long, callback: () -> Unit) {
        Simulator.runAfter(durationMillis) { callback() }
    }
    // setInterval
    private fun setInterval(periodMillis: Long, callback: () -> Unit) {
        setTimeout(periodMillis) {
            callback()
            setInterval(periodMillis, callback)
        }
    }
}