package exercises.e0

import labutils.Scenarios
import labutils.robot.RobotBehavior
import java.awt.Color

fun main() {
    // Note: you can change the `behavior` argument to run a different behavior.
    // You can also change `width` and `height` to resize the GUI.
    Scenarios.OneRobotOneLight.runScenario(
        behavior = Tutorial.behavior,
        width = 600.0,
        height = 600.0,
    )
}

object Tutorial {
    // Note: `RobotBehavior` is a lambda that takes in input a `Robot`.
    // In Kotlin, lambda are created with curly braces { x -> ... }.
    // Note: all interactions with sensors and actuators are synchronous:
    // the robot will wait until the action is performed (i.e. time passes).
    val behavior: RobotBehavior = { robot ->
        println("[Init] Position=${robot.body.position} Angle=${robot.body.angle}")

        println("[Get Closest Light] ${robot.lightSensor.closestLight()}")

        println("[Before Moving] ${robot.body}")
        robot.motor.forward(distance = 300.0)
        println("[After Moving] ${robot.body}")

        println("[Before Switching] ${robot.led}")
        robot.led.switch(on = true, color = Color.GREEN)
        println("[After Switching] ${robot.led}")

        println("[Before Rotating] ${robot.body}")
        robot.spinMotor.rotate(angle = 90.0)
        println("[After Rotating] ${robot.body}")
    }
}