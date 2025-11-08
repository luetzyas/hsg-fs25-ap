package exercises.e2

import labutils.Scenarios
import labutils.robot.RobotBehavior
import labutils.simulator.Simulator
import java.awt.Color

fun main() {
    Scenarios.OneRobotOneLight.runScenario(
        behavior = Callback.blinking,
        width = 600.0,
        height = 600.0,
    )
}

object Callback {
    // Note: `setTimeout` enables asynchronous behaviors: calling `setTimeout` will return almost
    // immediately, while behaviors specified in the `callback` will run approximately after `periodMillis`.
    fun setTimeout(durationMillis: Long, callback: suspend () -> Unit) =
        // Take this implementation for granted
        Simulator.runAfter(durationMillis) { callback() }

    fun setInterval(periodMillis: Long, callback: suspend () -> Unit) {
        // TODO Missing Implementation: execute `callback` after every `periodMillis`
        //      (hint: use `setTimeout` repeatedly)
    }

    val blinking: RobotBehavior = { robot ->
        // TODO Missing Behavior: define `blinking` using `setInterval`
    }

    val followingTheLight: RobotBehavior = { robot ->
        // TODO Missing Behavior: define `followingTheLight` using `setInterval`
        //      (hint: `followingTheLight` is the same as iterating reaching the light)
    }

    // Note: composition is much nicer because we designed behaviors to be asynchronous
    val blinkingAndFollowingTheLight: RobotBehavior = { robot ->
        blinking(robot)
        followingTheLight(robot)
    }

    // TODO Here is an example of callback hell. How can we improve the following code?
    fun blinkingSOS(): RobotBehavior = { robot ->
        // Note: the SOS signal can be represented as three short signals, followed by three
        // long signals, followed by another three short signals: - - - -- -- -- - - -
        setTimeout(100) {
            robot.led.switch(on = true, color = Color.RED)
            setTimeout(100) {
                robot.led.switch(on = false, color = Color.RED)
                setTimeout(100) {
                    robot.led.switch(on = true, color = Color.RED)
                    setTimeout(100) {
                        robot.led.switch(on = false, color = Color.RED)
                        setTimeout(100) {
                            robot.led.switch(on = true, color = Color.RED)
                            setTimeout(100) {
                                robot.led.switch(on = false, color = Color.RED)
                                setTimeout(200) {
                                    robot.led.switch(on = true, color = Color.GREEN)
                                    setTimeout(200) {
                                        robot.led.switch(on = false, color = Color.GREEN)
                                        setTimeout(200) {
                                            robot.led.switch(on = true, color = Color.GREEN)
                                            setTimeout(200) {
                                                robot.led.switch(on = false, color = Color.GREEN)
                                                setTimeout(200) {
                                                    robot.led.switch(on = true, color = Color.GREEN)
                                                    setTimeout(200) {
                                                        robot.led.switch(on = false, color = Color.GREEN)
                                                        setTimeout(100) {
                                                            robot.led.switch(on = true, color = Color.RED)
                                                            setTimeout(100) {
                                                                robot.led.switch(on = false, color = Color.RED)
                                                                setTimeout(100) {
                                                                    robot.led.switch(on = true, color = Color.RED)
                                                                    setTimeout(100) {
                                                                        robot.led.switch(on = false, color = Color.RED)
                                                                        setTimeout(100) {
                                                                            robot.led.switch(on = true, color = Color.RED)
                                                                            setTimeout(100) {
                                                                                robot.led.switch(on = false, color = Color.RED)
                                                                                setTimeout(1000){ blinkingSOS()(robot) }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Note: `blinkingSOS` is a function that returns a `RobotBehavior`. `RobotBehavior` is itself
    // a function that consumes a robot. This enables the `curried` syntax `blinkingSOS()(robot)`.
    val blinkingSOSAndFollowingTheLight: RobotBehavior = { robot ->
        blinkingSOS()(robot)
        followingTheLight(robot)
    }
}
