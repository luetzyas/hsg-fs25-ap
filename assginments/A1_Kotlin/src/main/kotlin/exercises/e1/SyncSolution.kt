package exercises.e1

import labutils.Scenarios
import labutils.robot.RobotBehavior

fun main() {
    Scenarios.OneRobotOneLight.runScenario(
        behavior = SyncSolution.switchOn,
        width = 600.0,
        height = 600.0,
    )
}

object SyncSolution {
    val switchOn: RobotBehavior = { robot ->
        // TODO Missing Behavior: switch on the led on the robot
        //      (hint: use `robot.led` to access the led)
        robot.led.switch(on = true)
    }

    val blinkOnce: RobotBehavior = { robot ->
        // TODO Missing Behavior: blink the led on the robot once for 500ms
        //      (hint: use `Thread.sleep` to wait for time to pass)
        robot.led.switch(on = true)
        Thread.sleep(500)
        robot.led.switch(on = false)
        Thread.sleep(500)
    }

    val reachTheLight: RobotBehavior = { robot ->
        // TODO Missing Behavior: look at the closest light and reach it
        //      (hint: use `robot.lightSensor` to get the direction and distance of the closest light)
        //      (hint: use `robot.spinMotor` to rotate towards the direction and `robot.motor` to move forward)
        val closestLight = robot.lightSensor.closestLight()
        if (closestLight != null) {
            robot.spinMotor.rotate(closestLight.direction.angle)
            robot.motor.forward(closestLight.direction.length)
        }
    }

    // Note: behaviors are functions and can be applied to `Robot`s
    val blinking: RobotBehavior = { robot ->
        while(true) { blinkOnce(robot) }
    }

    val followingTheLight: RobotBehavior = { robot ->
        while(true) { reachTheLight(robot) }
    }

    // TODO What happens here?
    // Answer: the robot only blinks without following the light. This happens because `blinking` is
    // a synchronous task and the robot will wait for it to finish. However, `blinking` is designed
    // to never finish, so the robot will never follow the light.
    val brokenBlinkingAndFollowingTheLight: RobotBehavior = { robot ->
        blinking(robot)
        followingTheLight(robot)
    }

    val blinkingAndFollowingTheLight: RobotBehavior = { robot ->
        // TODO Missing Behavior: how can the robot blink and follow the light at the same time?
        // Answer: it is possible to compose synchronous behaviors... but not immediately straightforward.
        // The complexity arises because you need to define how different behaviors interleave yourself.
        // In other words, you also have to define the *scheduler* for the behaviors.
        // Some micro-controllers have to rely on this synchronous architecture (often called Superloop),
        // because they do not support signals/interrupts (and therefore asynchronous programming).
        fun now(): Long = System.currentTimeMillis()
        var lastBlink = now()
        var lastMovement = now()

        while (true) {
            // BLINKING BEHAVIOR
            // Note: we don't use Thread.sleep because it may take too
            // long for the robot to rotate and move properly
            val timeSinceLastBlink = now() - lastBlink
            if (timeSinceLastBlink > 500.0) {
                robot.led.switch(on = !robot.led.isLit())
                lastBlink = now()
            }
            // FOLLOWING THE LIGHT BEHAVIOR
            // Note: we need to move a little at a time otherwise
            // it may take too long for the robot to blink properly
            val timeSinceLastMovement = now() - lastMovement
            if (timeSinceLastMovement > 10.0) {
                val closestLight = robot.lightSensor.closestLight()
                if (closestLight != null) {
                    robot.spinMotor.rotate(0.2 * closestLight.direction.angle)
                    robot.motor.forward(0.1 * closestLight.direction.length)
                    lastMovement = now()
                }
            }
            // TODO What happens if one behavior takes a really long time?
            // THINKING BEHAVIOR
            // Thread.sleep(3000)  // thinking about something...
        }
    }
}