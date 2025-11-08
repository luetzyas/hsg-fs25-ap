package exercises.e1

import labutils.Scenarios
import labutils.robot.Robot
import labutils.robot.RobotBehavior

fun main() {
    Scenarios.OneRobotOneLight.runScenario(
        behavior = Sync.switchOn,
        width = 600.0,
        height = 600.0,
    )
}

object Sync {
    val switchOn: RobotBehavior = { robot ->
        // TODO Missing Behavior: switch on the led on the robot
        //      (hint: use `robot.led` to access the led)
    }

    val blinkOnce: RobotBehavior = { robot ->
        // TODO Missing Behavior: blink the led on the robot once for 500ms
        //      (hint: use `Thread.sleep` to wait for time to pass)
    }

    val reachTheLight: RobotBehavior = { robot ->
        // TODO Missing Behavior: look at the closest light and reach it
        //      (hint: use `robot.lightSensor` to get the direction and distance of the closest light)
        //      (hint: use `robot.spinMotor` to rotate towards the direction and `robot.motor` to move forward)
    }

    // Note: behaviors are functions and can be applied to `Robot`s
    val blinking: RobotBehavior = { robot ->
        while(true) { blinkOnce(robot) }
    }

    val followingTheLight: RobotBehavior = { robot ->
        while(true) { reachTheLight(robot) }
    }

    // TODO What happens here?
    val brokenBlinkingAndFollowingTheLight: RobotBehavior = { robot ->
        blinking(robot)
        followingTheLight(robot)
    }

    val blinkingAndFollowingTheLight: RobotBehavior = { robot ->
        // TODO Missing Behavior: how can the robot blink and follow the light at the same time?
    }
}