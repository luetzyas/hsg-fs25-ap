package exercises.e2

import labutils.Scenarios
import labutils.robot.RobotBehavior
import labutils.simulator.Simulator
import java.awt.Color

fun main() {
    Scenarios.OneRobotOneLight.runScenario(
        behavior = CallbackSolution.blinkingAndFollowingTheLight,
        width = 600.0,
        height = 600.0,
    )
}

object CallbackSolution {
    // Note: `setTimeout` enables asynchronous behaviors: calling `setTimeout` will return almost
    // immediately, while behaviors specified in the `callback` will run approximately after `periodMillis`.
    fun setTimeout(durationMillis: Long, callback: suspend () -> Unit) =
        // Take this implementation for granted
        Simulator.runAfter(durationMillis) { callback() }

    fun setInterval(periodMillis: Long, callback: suspend () -> Unit) {
        // TODO Missing Implementation: execute `callback` after every `periodMillis`
        //      (hint: use `setTimeout` repeatedly)
        // Note: in Kotlin, if the last argument is a lambda, it can be passed by opening a new scope
        setTimeout(periodMillis) {
            callback()
            setInterval(periodMillis, callback)
        }
    }

    val blinking: RobotBehavior = { robot ->
        // TODO Missing Behavior: define `blinking` using `setInterval`
        setInterval(500){
            robot.led.switch(on = !robot.led.isLit())
        }
    }

    val followingTheLight: RobotBehavior = { robot ->
        // TODO Missing Behavior: define `followingTheLight` using `setInterval`
        //      (hint: `followingTheLight` is the same as iterating reaching the light)
        setInterval(10) {
            val closestLight = robot.lightSensor.closestLight()
            if (closestLight != null) {
               robot.spinMotor.rotate(0.2 * closestLight.direction.angle)
               robot.motor.forward(0.1 * closestLight.direction.length)
            }
        }
    }

    // Note: composition is much nicer because we designed behaviors to be asynchronous
    val blinkingAndFollowingTheLight: RobotBehavior = { robot ->
        blinking(robot)
        followingTheLight(robot)
    }

    // TODO Here is an example of callback hell. How can we improve the following code?
    // Answer: one solution is to refactor the code by splitting the behavior in different
    // functions. In practice, this means manually flattening the code by finding ad-hoc
    // designs. An example solution is shown in `FlattenedBlinkingSOS` below.
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

    // One solution to the callback hell above
    object FlattenedBlinkingSOS {
        fun blink(length: Long, color: Color, callback: RobotBehavior): RobotBehavior = { robot ->
            setTimeout(length) {
                robot.led.switch(on = true, color = color)
                setTimeout(length) {
                    robot.led.switch(on = false, color = color)
                    callback(robot)
                }
            }
        }
        fun shortBlink(callback: RobotBehavior): RobotBehavior =
            blink(100, Color.RED, callback)
        fun longBlink(callback: RobotBehavior): RobotBehavior =
            blink(200, Color.GREEN, callback)
        fun blinkS(callback: RobotBehavior): RobotBehavior =
            shortBlink(shortBlink(shortBlink(callback)))
        fun blinkO(callback: RobotBehavior): RobotBehavior =
            longBlink(longBlink(longBlink(callback)))
        fun blinkingSOS(): RobotBehavior =
            blinkS(blinkO(blinkS { robot -> setTimeout(1000){ blinkingSOS()(robot) } }))
        val blinkingSOSAndFollowingTheLight: RobotBehavior = { robot ->
            blinkingSOS()(robot)
            followingTheLight(robot)
        }
    }
}
