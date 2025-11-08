package exercises.e1

import labutils.Scenarios
import labutils.robot.Robot
import labutils.robot.RobotBehavior
import kotlin.system.exitProcess

fun main() {
    Scenarios.OneRobotOneLight.runScenario(
        //behavior = Sync.switchOn, //Task 2
        //behavior = Sync.blinkOnce, //Task 3
        //behavior = Sync.reachTheLight, //Task 4
        //behavior = Sync.brokenBlinkingAndFollowingTheLight, //Task 5
        behavior = Sync.blinkingAndFollowingTheLight, //Task 6
        width = 600.0,
        height = 600.0,
    )
}

object Sync {
    //Task 2
    val switchOn: RobotBehavior = { robot ->
        // TODO Missing Behavior: switch on the led on the robot
        //      (hint: use `robot.led` to access the led)
        robot.led.switch(on = true)
    }
    //Task 3
    val blinkOnce: RobotBehavior = { robot ->
        // TODO Missing Behavior: blink the led on the robot once for 500ms
        //      (hint: use `Thread.sleep` to wait for time to pass)
        robot.led.switch(on = true) //switch on
        Thread.sleep(500) //wait 0.5s
        robot.led.switch(on = false) //switch off
        Thread.sleep(500) //wait 0.5s
    }
    //Task 4
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
    //Task 5
    // Note: behaviors are functions and can be applied to `Robot`s
    val blinking: RobotBehavior = { robot ->
        while(true) { blinkOnce(robot) }
    }
    //Task 5
    val followingTheLight: RobotBehavior = { robot ->
        while(true) { reachTheLight(robot) }
    }
    //Task 5
    // TODO What happens here?
    // The robot only blinks without following the light. This happens because `blinking` is a synchronous task and the robot will wait for it to finish. However, since `blinking` contains an infinite loop, it will never finish, and thus `followingTheLight` will never be executed.
    val brokenBlinkingAndFollowingTheLight: RobotBehavior = { robot ->
        blinking(robot)
        followingTheLight(robot)
    }

    val blinkingAndFollowingTheLight: RobotBehavior = { robot ->
        // TODO Missing Behavior: how can the robot blink and follow the light at the same time?
        fun now(): Long = System.currentTimeMillis()
        var lastBlink = now()
        var lastMovement = now()

        while (true) {
            //BLINKING BEHAVIOR
            val timeSinceLastBlink = now() - lastBlink
            if (timeSinceLastBlink > 500.0) {
                robot.led.switch(on = !robot.led.isLit())
                lastBlink = now()
            }
            //FOLLOWING THE LIGHT BEHAVIOR
            val timeSinceLastMovement = now() - lastMovement
            if (timeSinceLastMovement > 10.0) {
                val closestLight = robot.lightSensor.closestLight()
                if (closestLight != null) {
                    robot.spinMotor.rotate(0.2 * closestLight.direction.angle)
                    robot.motor.forward(0.1 * closestLight.direction.length)
                    lastMovement = now()
                }
            }
            // THINKING BEHAVIOR
            // Thread.sleep(3000)
        }
    }
}