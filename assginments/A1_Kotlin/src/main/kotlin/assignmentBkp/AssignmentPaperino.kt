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
        redBehavior = AssignmentPaperino.redBehavior,
        greenBehavior = AssignmentPaperino.greenBehavior,
        blueBehavior = AssignmentPaperino.blueBehavior,
        width = 600.0,
        height = 600.0
    )
}

@OptIn(ObsoleteCoroutinesApi::class)
object AssignmentPaperino {
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
    // BUILD BEHAVIOR FOR A ROBOT OF A GIVEN COLOR
    // ---------------------------------------------------------
    private fun buildBehavior(currColor: Color, name: String): RobotBehavior = { robot ->
        // declared state variables
        var reached = false
        var celebrating = false
        val ownColorPercepts = mutableSetOf<Point>() // Set für automatische Duplikatsvermeidung
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
                        // Wenn die Nachricht die eigene Farbe betrifft, zum Set hinzufügen (automatisch dedupliziert)
                        if (msg.color == currColor) {
                            val wasAdded = ownColorPercepts.add(msg.position)
                            if (wasAdded) {
                                println("$name added own color percept at ${msg.position}, total: ${ownColorPercepts.size}")
                            }
                        }
                    }
                    is TargetLightFound -> {
                        // Nicht mehr verwendet in diesem Flow
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
        // Task 2: Coordination
        // -----------------------------------------------------
        /*setInterval(200) {
            Simulator.async {
                val closest = robot.lightSensor.closestLight()
                if (closest != null) {
                    val robotPos = robot.body.position
                    val dir = closest.direction
                    val pos = Point(
                        robotPos.x + dir.length * cos(dir.angle),
                        robotPos.y + dir.length * sin(dir.angle)
                    )
                    channels[currColor]?.send(
                        LightSeen(closest.color, pos)
                    )
                }
            }
        }
        */
        setInterval(200) {
            Simulator.async {
                val closest = robot.lightSensor.closestLight()
                if (closest != null) {
                    val robotPos = robot.body.position
                    val dir = closest.direction
                    val pos = Point(
                        robotPos.x + dir.length * cos(dir.angle),
                        robotPos.y + dir.length * sin(dir.angle)
                    )

                    // LightSeen an alle Roboter senden
                    channels.values.forEach { ch ->
                        ch.send(LightSeen(closest.color, pos))
                    }
                }
            }
        }

        // -----------------------------------------------------
        // Task 3: Reach the Light - Move towards center of own color percepts
        // -----------------------------------------------------
        setInterval(150) {
            Simulator.async {
                if (!reached && ownColorPercepts.isNotEmpty()) {
                    // Berechne den Mittelpunkt aller eigenen Farbwahrnehmungen
                    val centerX = ownColorPercepts.map { it.x }.average()
                    val centerY = ownColorPercepts.map { it.y }.average()
                    val centerPoint = Point(centerX, centerY)

                    val rel = robot.body.relativeDirection(centerPoint)
                    val dist = rel.length
                    val angle = rel.angle

                    println("$name: center=$centerPoint, dist=$dist, angle=$angle, percepts=${ownColorPercepts.size}")

                    // Check ob Ziel erreicht (ZUERST prüfen, bevor Bewegung)
                    if (dist < 10) {
                        reached = true
                        println("$name reached the center of its color light!")
                        robot.led.switch(on = true, color = currColor)
                        channels.values.forEach { it.send(GoalReached(currColor)) }
                    } else {
                        // Bewege dich zum Mittelpunkt
                        Simulator.async { robot.spinMotor.rotate(0.2 * angle) }
                        Simulator.async { robot.motor.forward(0.1 * dist) }
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
