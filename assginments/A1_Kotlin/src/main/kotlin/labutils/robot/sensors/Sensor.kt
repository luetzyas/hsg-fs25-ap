package labutils.robot.sensors

import labutils.robot.Robot
import labutils.robot.RobotComponent

/** A [Sensor] perceives data of type [D] from the environment. */
abstract class Sensor<D>: RobotComponent() {
    /**
     * Get the most recent perception of this [Sensor], if any.
     * The perception may not be up-to-date with the current time.
     * For this reason, [Robot]s should use [sense] instead.
     */
    abstract fun lastSensed(): D?

    /**
     * @return the up-to-date data perceived from this [Sensor],
     *         or null if no data is perceived.
     */
    abstract suspend fun sense(): D?
}