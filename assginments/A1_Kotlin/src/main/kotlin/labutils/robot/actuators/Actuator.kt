package labutils.robot.actuators

import labutils.robot.RobotComponent

/** An [Actuator] performs a task that modifies the environment. */
abstract class Actuator<I>: RobotComponent() {
    /**
     * Start a new task for the actuator to perform.
     * @param input the input for the actuator to perform its task.
     */
    abstract suspend fun act(input: I)
}