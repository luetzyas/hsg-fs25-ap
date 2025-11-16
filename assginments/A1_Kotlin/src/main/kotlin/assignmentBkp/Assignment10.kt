package assignmentBkp

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import labutils.Scenarios
import labutils.math.Point
import labutils.robot.RobotBehavior
import labutils.simulator.Simulator
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin

fun main() {
    Scenarios.Assignment.runScenario(
        redBehavior = Assignment10.redBehavior,
        greenBehavior = Assignment10.greenBehavior,
        blueBehavior = Assignment10.blueBehavior,
        width = 600.0,
        height = 600.0
    )
}

@OptIn(ObsoleteCoroutinesApi::class)
object Assignment10 {
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
        val closest = robot.lightSensor.closestLight()
        // declared state variables
        var targetLightFound: Point? = null
        var targetLight : Point? = null
        var reached = false
        var celebrating = false
        val knownLights = mutableMapOf<Color, Point>()
        val reachedMap = mutableMapOf(
            Color.RED to false,
            Color.GREEN to false,
            Color.BLUE to false
        )
        println("Hi I'm $name")
        channels[currColor] = Simulator.Companion.actor {
            for (msg in channel) {
                when (msg) {
                    is LightSeen -> {
                        if (currColor != msg.color) {
                            println("$name saw ${colorName(msg.color)} at ${msg.position}")
                            channels[msg.color]?.let { receiver ->
                                Simulator.Companion.async {
                                    receiver.send(TargetLightFound(msg.color, msg.position))
                                }
                            }
                        }
                    }
                    is TargetLightFound -> {
                        /*if (msg.target == currColor && targetLightFound == null) {
                            targetLightFound = msg.position
                            println("$name received its true target position $targetLightFound")
                        }*/
                        // calculate average of all coordinates
                        knownLights[msg.target] = msg.position
                        if (knownLights.size == 3) {
                            val avgX = knownLights.values.map { it.x }.average()
                            val avgY = knownLights.values.map { it.y }.average()
                            targetLight = Point(avgX, avgY)
                            println("$name computed average target position $targetLight")
                        }
                    }
                    is GoalReached -> {
                        reachedMap[msg.color] = true
                        val allDone = reachedMap.values.all { it }
                        if (allDone && reached) {
                            channels.values.forEach { ch ->
                                Simulator.Companion.async { ch.send(Celebration) }
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
            Simulator.Companion.async {
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
        // Task 3: Reach the Light
        // -----------------------------------------------------
        setInterval(150) {
            Simulator.Companion.async {
                val target = targetLightFound
                if (target != null && !reached) {

                    val rel = robot.body.relativeDirection(target)
                    val dist = rel.length
                    val angle = rel.angle

                    // Phase 1–2: grobe Zielverfolgung
                    if (dist > 120) {
                        robot.spinMotor.rotate(0.2 * angle)
                        robot.motor.forward(0.3 * dist)
                        return@async
                    }

                    // Phase 3: wir sind nah genug -> AsyncAwait Steuerung
                    val closest = robot.lightSensor.closestLight()
                    if (closest != null && closest.color == currColor) {

                        val fineAngle = closest.direction.angle
                        val fineDist = closest.direction.length

                        // gleiche Steuerung wie AsyncAwait.kt
                        Simulator.Companion.async { robot.spinMotor.rotate(0.2 * fineAngle) }
                        Simulator.Companion.async { robot.motor.forward(0.1 * fineDist) }

                        // check erreicht
                        if (fineDist < 10) {
                            reached = true
                            println("$name reached its true light!")
                            robot.led.switch(on = true, color = currColor)
                            channels.values.forEach { it.send(GoalReached(currColor)) }
                        }
                        return@async
                    }

                    // kein eigenes Licht gesehen -> weiter grob orientieren
                    robot.spinMotor.rotate(0.2 * angle)
                    robot.motor.forward(0.3 * dist)
                }
            }
        }
        // -----------------------------------------------------
        // Task 4: Celebration
        // -----------------------------------------------------
        var ledOn = false
        setInterval(500) {
            if (celebrating) {
                Simulator.Companion.async {
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
        Simulator.Companion.runAfter(ms) { f() }
    }

    // setInterval
    private fun setInterval(ms: Long, f: () -> Unit) {
        setTimeout(ms) {
            f()
            setInterval(ms, f)
        }
    }
}