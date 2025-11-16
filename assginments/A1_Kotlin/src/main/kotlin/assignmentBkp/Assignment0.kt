package assignment

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import labutils.*
import labutils.robot.RobotBehavior
import java.awt.Color

fun main() {
    Scenarios.Assignment.runScenario(
        redBehavior = Assignment0.redBehavior,
        greenBehavior = Assignment0.greenBehavior,
        blueBehavior = Assignment0.blueBehavior,
        width = 600.0,
        height = 600.0,
    )
}

@OptIn(ObsoleteCoroutinesApi::class)
object Assignment0 {
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
    // TODO Missing Messages

    // BEHAVIORS
    val redBehavior: RobotBehavior = { robot ->
        // TODO Missing Behavior
    }
    val greenBehavior: RobotBehavior = { robot ->
        // TODO Missing Behavior
    }
    val blueBehavior: RobotBehavior = { robot ->
        // TODO Missing Behavior
    }
}