package labutils

import labutils.environment.Environment
import labutils.environment.EnvironmentConfiguration
import labutils.environment.Light
import labutils.gui.RobotSimulatorGUI
import labutils.math.Point
import labutils.robot.Robot
import labutils.robot.RobotBehavior
import labutils.robot.RobotBody
import labutils.robot.actuators.Motor
import labutils.robot.actuators.SpinMotor
import labutils.simulator.Simulator
import java.awt.Color

object Scenarios {
    object OneRobotOneLight {
        fun runScenario(behavior: RobotBehavior = {}, width: Double = 800.0, height: Double = 600.0) {
            val robot = Robot(RobotBody(position = Point(x = 0.2 * width, y = 0.2 * height)))
            val light = Light(position = Point(x = 0.8 * width, y = 0.8 * height))
            val configuration = EnvironmentConfiguration(width, height)
            val environment = Environment(configuration).withRobots(robot).withLights(light)
            val view = RobotSimulatorGUI(environment)
            val simulator = Simulator(environment, view.ticks())
            simulator.schedule(robot, behavior)
        }
    }
    object PreyPredator {
        fun runScenario(
            preyBehavior: RobotBehavior = {}, predatorBehavior: RobotBehavior = {},
            width: Double = 800.0, height: Double = 600.0
        ) {
            val predator = Robot(
                body = RobotBody(position = Point(x = 0.25 * width, 0.75 * height), bodyColor = Color.RED)
            )
            val prey = Robot(
                body = RobotBody(position = Point(x = 0.5 * width, 0.75 * height), bodyColor = Color.GREEN),
                motor = Motor(1.25),
                spinMotor = SpinMotor(speed = 0.5)
            )
            val configuration = EnvironmentConfiguration(width, height)
            val environment = Environment(configuration).withRobots(prey, predator)
            val view = RobotSimulatorGUI(environment)
            val simulator = Simulator(environment, view.ticks())
            simulator.schedule(prey, preyBehavior)
            simulator.schedule(predator, predatorBehavior)
        }
    }
    object Assignment {
        fun runScenario(
            redBehavior: RobotBehavior = {}, greenBehavior: RobotBehavior = {}, blueBehavior: RobotBehavior = {},
            width: Double = 800.0, height: Double = 800.0
        ) {
            val redRobot = Robot(RobotBody(Point(0.3 * width, 0.25 * height), bodyColor = Color.RED))
            val greenRobot = Robot(RobotBody(Point(0.7 * width, 0.25 * height), bodyColor = Color.GREEN))
            val blueRobot = Robot(RobotBody(Point(0.5 * width, 0.7 * height), bodyColor = Color.BLUE))
            val redLight = Light(Point(0.85 * width, 0.25 * height), color = Color.RED)
            val greenLight = Light(Point(0.5 * width, 0.85 * height), color = Color.GREEN)
            val blueLight = Light(Point(0.15 * width, 0.25 * height), color = Color.BLUE)
            val configuration = EnvironmentConfiguration(width, height)
            val environment = Environment(configuration).withRobots(redRobot, greenRobot, blueRobot).withLights(redLight, greenLight, blueLight)
            val view = RobotSimulatorGUI(environment)
            val simulator = Simulator(environment, view.ticks())
            simulator.schedule(redRobot, redBehavior)
            simulator.schedule(greenRobot, greenBehavior)
            simulator.schedule(blueRobot, blueBehavior)
        }
    }
}