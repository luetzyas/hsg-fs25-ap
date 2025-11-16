package assignmentBkp

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import labutils.*
import labutils.robot.RobotBehavior
import labutils.simulator.Simulator
import java.awt.Color

fun main() {
    Scenarios.Assignment.runScenario(
        redBehavior = Assignment1.redBehavior,
        greenBehavior = Assignment1.greenBehavior,
        blueBehavior = Assignment1.blueBehavior,
        width = 600.0,
        height = 600.0,
    )
}

@OptIn(ObsoleteCoroutinesApi::class)
object Assignment1 {
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
    private val robotIds: List<Color> = listOf(Color.RED, Color.GREEN, Color.BLUE)
    private val channels: MutableMap<Color, SendChannel<Message>> = mutableMapOf()

    // MESSAGES
    private sealed class Message
    // Task 1: Designing Actors
    private data class LightSeen(val color: Color, val position: labutils.math.Point) : Message()
    private data class TargetLightFound(val target: Color, val position: labutils.math.Point) : Message()
    private data class GoalReached(val color: Color) : Message()
    private data object Celebration : Message()

    // BEHAVIORS
    val redBehavior: RobotBehavior = { robot ->
        // name actor :)
        var name = "REDly"
        println("Hi! I'm $name")
        // set defaults
        var currColor = Color.RED
        var targetLightPosition: labutils.math.Point? = null
        var reached = false //set default
        val knownLights = mutableMapOf<Color, labutils.math.Point>()
        val othersReached = mutableMapOf(
            Color.RED to false,
            Color.GREEN to false,
            Color.BLUE to false,
        )
        // create Actor
        channels[currColor] = labutils.simulator.Simulator.actor {
            for (msg in channel) {
                when (msg) {
                    is LightSeen -> {
                        //nearest light seen
                        knownLights[msg.color] = msg.position
                        println("$name: has seen color ${colorName(msg.color)} at ${msg.position}")
                        // check msg.color for red
                        if (msg.color == currColor) {
                            targetLightPosition = msg.position
                            println("$name's target light was reported at $targetLightPosition")
                        }
                    }
                    is TargetLightFound -> {
                        if (msg.target == currColor) {
                            targetLightPosition = msg.position
                            print("$name: has found target light ${msg.position}")
                        }
                    }
                    is GoalReached -> {
                        othersReached[msg.color] = true
                        println("$name: has reached the Goal ${colorName(msg.color)}")
                        // check if all reached
                        var allReached = othersReached.values.all { it }
                        if (allReached && reached) {
                            // send Celebration to all robots
                            channels.values.forEach { ch ->
                                ch.send(Celebration)
                            }
                        }
                    }
                    is Celebration -> {
                        println("Let's celebrate $name!")
                    }
                    else -> println("No behavior for $name defined for message: $msg")
                }
            }
        }
        // Task 2: Coordination
        setInterval(200) {
            Simulator.async {
                val closestLight = robot.lightSensor.closestLight()
                if (closestLight != null) {
                    // absolute position from simulator (correct!)
                    val pos = robot.body.position
                    // send LightSeen to all actors
                    channels.values.forEach { ch ->
                        ch.send(LightSeen(closestLight.color, pos))
                    }
                }
            }
        }
        // Task 3: Reach the Light
        setInterval(150) {
            Simulator.async {
                if (targetLightPosition != null && !reached) {
                    val target = targetLightPosition!!
                    val dir = robot.body.relativeDirection(target)
                    // move to target
                    if (dir.length < 10) {
                        reached = true
                        println("$name reached its target light")
                        // send msg to all robots
                        channels.values.forEach { ch ->
                            ch.send(GoalReached(currColor))
                        }
                        robot.led.switch(on = true, color = currColor)
                    } else {
                        robot.spinMotor.rotate(dir.angle)
                        robot.motor.forward(dir.length)
                    }
                }
            }
        }
    }

    val greenBehavior: RobotBehavior = { robot ->
        // name actor :)
        var name = "GREENaldo"
        println("Hi! I'm $name")
        // set defaults
        var currColor = Color.GREEN
        var targetLightPosition: labutils.math.Point? = null
        var reached = false //set default
        val knownLights = mutableMapOf<Color, labutils.math.Point>()
        val othersReached = mutableMapOf(
            Color.RED to false,
            Color.GREEN to false,
            Color.BLUE to false,
        )
        // create Actor
        channels[currColor] = labutils.simulator.Simulator.actor {
            for (msg in channel) {
                when (msg) {
                    is LightSeen -> {
                        //nearest light seen
                        knownLights[msg.color] = msg.position
                        println("$name: has seen color ${colorName(msg.color)} at ${msg.position}")
                        // check msg.color for red
                        if (msg.color == currColor) {
                            targetLightPosition = msg.position
                            println("$name's target light was reported at $targetLightPosition")
                        }
                    }
                    is TargetLightFound -> {
                        if (msg.target == currColor) {
                            targetLightPosition = msg.position
                            print("$name: has found target light ${msg.position}")
                        }
                    }
                    is GoalReached -> {
                        othersReached[msg.color] = true
                        println("$name: has reached the Goal ${colorName(msg.color)}")
                        // check if all reached
                        var allReached = othersReached.values.all { it }
                        if (allReached && reached) {
                            // send Celebration to all robots
                            channels.values.forEach { ch ->
                                ch.send(Celebration)
                            }
                        }
                    }
                    is Celebration -> {
                        println("Let's celebrate $name!")
                    }
                    else -> println("No behavior for $name defined for message: $msg")
                }
            }
        }
        // Task 2: Communicating Robots
        setInterval(200) {
            Simulator.async {
                val closestLight = robot.lightSensor.closestLight()
                if (closestLight != null) {
                    // absolute position from simulator (correct!)
                    val pos = robot.body.position
                    // send LightSeen to all actors
                    channels.values.forEach { ch ->
                        ch.send(LightSeen(closestLight.color, pos))
                    }
                }
            }
        }
        // Task 3: Reach the Light
        setInterval(150) {
            Simulator.async {
                if (targetLightPosition != null && !reached) {
                    val target = targetLightPosition!!
                    val dir = robot.body.relativeDirection(target)
                    // move to target
                    if (dir.length < 10) {
                        reached = true
                        println("$name reached its target light")
                        // send msg to all robots
                        channels.values.forEach { ch ->
                            ch.send(GoalReached(currColor))
                        }
                        robot.led.switch(on = true, color = currColor)
                    } else {
                        robot.spinMotor.rotate(dir.angle)
                        robot.motor.forward(dir.length)
                    }
                }
            }
        }
    }

    val blueBehavior: RobotBehavior = { robot ->
        // name actor :)
        var name = "BLUEbert"
        println("Hi! I'm $name")
        // set defaults
        var currColor = Color.BLUE
        var targetLightPosition: labutils.math.Point? = null
        var reached = false //set default
        val knownLights = mutableMapOf<Color, labutils.math.Point>()
        val othersReached = mutableMapOf(
            Color.RED to false,
            Color.GREEN to false,
            Color.BLUE to false,
        )
        // create Actor
        channels[currColor] = labutils.simulator.Simulator.actor {
            for (msg in channel) {
                when (msg) {
                    is LightSeen -> {
                        //nearest light seen
                        knownLights[msg.color] = msg.position
                        println("$name: has seen color ${colorName(msg.color)} at ${msg.position}")
                        // check msg.color for red
                        if (msg.color == currColor) {
                            targetLightPosition = msg.position
                            println("$name's target light was reported at $targetLightPosition")
                        }
                    }
                    is TargetLightFound -> {
                        if (msg.target == currColor) {
                            targetLightPosition = msg.position
                            print("$name: has found target light ${msg.position}")
                        }
                    }
                    is GoalReached -> {
                        othersReached[msg.color] = true
                        println("$name: has reached the Goal ${colorName(msg.color)}")
                        // check if all reached
                        var allReached = othersReached.values.all { it }
                        if (allReached && reached) {
                            // send Celebration to all robots
                            channels.values.forEach { ch ->
                                ch.send(Celebration)
                            }
                        }
                    }
                    is Celebration -> {
                        println("Let's celebrate $name!")
                    }
                    else -> println("No behavior for $name defined for message: $msg")
                }
            }
        }
        // Task 2: Communicating Robots
        setInterval(200) {
            Simulator.async {
                val closestLight = robot.lightSensor.closestLight()
                if (closestLight != null) {
                    // absolute position from simulator (correct!)
                    val pos = robot.body.position
                    // send LightSeen to all actors
                    channels.values.forEach { ch ->
                        ch.send(LightSeen(closestLight.color, pos))
                    }
                }
            }
        }
        // Task 3: Reach the Light
        setInterval(150) {
            Simulator.async {
                if (targetLightPosition != null && !reached) {
                    val target = targetLightPosition!!
                    val dir = robot.body.relativeDirection(target)
                    // move to target
                    if (dir.length < 10) {
                        reached = true
                        println("$name reached its target light")
                        // send msg to all robots
                        channels.values.forEach { ch ->
                            ch.send(GoalReached(currColor))
                        }
                        robot.led.switch(on = true, color = currColor)
                    } else {
                        robot.spinMotor.rotate(dir.angle)
                        robot.motor.forward(dir.length)
                    }
                }
            }
        }
    }

    // 1. setTimeout from e2 CallbackSolution
    private fun setTimeout(durationMillis: Long, callback: () -> Unit) {
        labutils.simulator.Simulator.runAfter(durationMillis) {
            callback()
        }
    }
    // 2. setInterval from e2 CallbackSolution
    private fun setInterval(periodMillis: Long, callback: () -> Unit) {
        setTimeout(periodMillis) {
            callback()
            setInterval(periodMillis, callback)
        }
    }
    // helper for readable color names
    private fun colorName(color: Color): String =
        when (color) {
            Color.RED -> "RED"
            Color.GREEN -> "GREEN"
            Color.BLUE -> "BLUE"
            else -> "UNKNOWN"
        }
}

