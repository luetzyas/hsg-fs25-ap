package labutils.robot.sensors

import labutils.environment.Light
import labutils.environment.Environment
import labutils.math.Vector
import java.awt.Color

/**
 * A [Sensor] for detecting sources of lights, their direction and their
 * distance with respect to the owner robot.
 */
data class LightSensor(val radius: Double) : JobSensor<LightSensor.DirectionalLight>() {
    /** The perception of a [Light] at a specific direction and distance. */
    data class DirectionalLight(val direction: Vector, val color: Color)

    /**
     * Alias for [sense].
     *
     * @return the light closest to the owner robot, or null if no
     *         light is perceived. The sensor detects the color,
     *         direction and the distance from the light.
     */
    suspend fun closestLight(): DirectionalLight? = this.sense()

    override suspend fun tickSense(environment: Environment, tick: Tick): DirectionalLight? =
        environment
            .lights()
            .map { Pair(it, this.owner.body.position.squareDistance(it.position)) }
            .minByOrNull { it.second }
            ?.first?.let { DirectionalLight(this.owner.body.relativeDirection(it.position), it.color) }
}