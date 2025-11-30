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
        redBehavior = Assignment.redBehavior,
        greenBehavior = Assignment.greenBehavior,
        blueBehavior = Assignment.blueBehavior,
        width = 450.0,
        height = 400.0
    )
}

@OptIn(ObsoleteCoroutinesApi::class)
object Assignment {
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


    // BEHAVIORS
    val redBehavior: RobotBehavior = { robot ->
        buildRobotBehavior(Color.RED, "REDly")(robot)
    }
    val greenBehavior: RobotBehavior = { robot ->
        buildRobotBehavior(Color.GREEN, "GREENaldo")(robot)
    }
    val blueBehavior: RobotBehavior = { robot ->
        buildRobotBehavior(Color.BLUE, "BLUEbert")(robot)
    }
    // *****************************************************
    // * TODO: BUILD BEHAVIOR FOR A ROBOT OF A GIVEN COLOR *
    // *****************************************************
    private fun buildRobotBehavior(currColor: Color, name: String): RobotBehavior = { robot ->
        // trace testing prints
        val test = true
        // declared state variables
        var targetLightFound: Point? = null
        var targetLight : Point? = null
        var reached = false
        var celebrating = false
        val reachedMap = mutableMapOf(
            Color.RED to false,
            Color.GREEN to false,
            Color.BLUE to false
        )
        println("Hi I'm $name")
        // ***********************************
        // * TODO Task1: Designing Actors    *
        // ***********************************
        channels[currColor] = Simulator.Companion.actor {
            for (msg in channel) {
                when (msg) {
                    is LightSeen -> {
                        if (test) {println("*LightSeen for $name started")}
                        if (currColor != msg.color) {
                            if (test) {println("$name saw ${colorName(msg.color)} at ${msg.position}")}
                            channels[msg.color]?.let { receiver ->
                                Simulator.Companion.async {
                                    receiver.send(TargetLightFound(msg.color, msg.position))
                                }
                            }
                        } else {
                            if (targetLight == null) {
                                targetLight = msg.position
                                println("$name stored its target light at $targetLight")
                            }
                        }
                    }
                    is TargetLightFound -> {
                        if (test) {println("*TargetLightFound for $name started")}
                        if (msg.target == currColor && targetLightFound == null) {
                            targetLightFound = msg.position
                            println("$name received its true target position $targetLightFound")
                        }
                    }
                    is GoalReached -> {
                        if (test) {println("*GoalReached for $name started")}
                        reachedMap[msg.color] = true
                        val allDone = reachedMap.values.all { it }
                        if (allDone && reached) {
                            channels.values.forEach { ch ->
                                Simulator.Companion.async { ch.send(Celebration) }
                            }
                        }
                    }
                    is Celebration -> {
                        if (test) {println("*Celebration for $name started")}
                        celebrating = true
                        println("$name starts blinking")
                    }
                }
            }
        }
        // *******************************
        // * TODO Task2: Coordination    *
        // *******************************
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
                    println("$name sees ${colorName(closest.color)} light at $pos")
                    channels[currColor]?.send(
                        LightSeen(closest.color, pos)
                    )
                }
            }
        }
        // *******************************
        // * TODO Task3: Reach the Light *
        // *******************************
        setInterval(150) {
            Simulator.Companion.async {
                val target = targetLightFound
                if (target != null && !reached) {
                    val rel = robot.body.relativeDirection(target)
                    val dist = rel.length
                    val angle = rel.angle
                    // already start moving
                    if (dist > 120) {
                        robot.spinMotor.rotate(0.2 * angle)
                        robot.motor.forward(0.3 * dist)
                        return@async
                    }
                    // move towards target
                    val closest = robot.lightSensor.closestLight()
                    if (closest != null && closest.color == currColor) {
                        println("$name closest: ${colorName(closest.color)} currColor: ${colorName(currColor)}")
                        //val fineAngle = closest.direction.angle
                        val fineDist = closest.direction.length
                        robot.spinMotor.rotate(0.2 * closest.direction.angle)
                        robot.motor.forward(0.1 * closest.direction.length)
                        // check if reached target position
                        if (fineDist < 1) {
                            reached = true
                            println("$name reached its true light!")
                            robot.led.switch(on = true, color = currColor)
                            channels.values.forEach { it.send(GoalReached(currColor)) }
                        }
                        return@async
                    }
                    // look around target colored light after movind towards target
                    robot.spinMotor.rotate(0.2 * angle)
                    robot.motor.forward(0.3 * dist)
                }
            }
        }
        // *******************************
        // * TODO Task4: Celebration     *
        // *******************************
        var ledOn = false
        setInterval(500) {
            if (celebrating) {
                Simulator.Companion.async {
                    ledOn = !ledOn
                    robot.led.switch(on = ledOn, color = currColor)
                }
            }
        }
        // Intervall to spin robot while celebrating
        setInterval(10) {
            if (celebrating) {
                Simulator.Companion.async {
                    robot.spinMotor.rotate(0.5 * Math.PI)
                }
            }
        }
    }

    // ******************
    // * TODO UTILITIES *
    // ******************
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
    // get Color String names for logging
    private fun colorName(c: Color) =
        when (c) { Color.RED -> "RED"; Color.GREEN -> "GREEN"; Color.BLUE -> "BLUE"; else -> "?" }

}