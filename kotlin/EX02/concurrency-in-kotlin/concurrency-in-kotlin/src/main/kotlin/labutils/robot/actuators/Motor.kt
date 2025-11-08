package labutils.robot.actuators

import kotlinx.coroutines.CompletableJob
import labutils.environment.Environment
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin

/** A [Motor] that can move the robot forward with the given speed. */
data class Motor(private val speed: Double) : JobActuator<Double>() {
    /**
     * Alias for [act].
     *
     * Set up a task that will move the owner robot forward of the
     * specified distance over time. A non-positive distance will
     * stop the movement.
     *
     * @param distance the specified distance.
     */
    suspend fun forward(distance: Double) = this.act(distance)

    override suspend fun tickJob(job: CompletableJob, state: Double, environment: Environment, tick: Tick): Double {
        if (state > 0) {
            val delta = kotlin.math.min(state, this.speed)
            this.owner.body.position.x += (delta * cos(toRadians(this.owner.body.angle)))
            this.owner.body.position.y += (-delta * sin(toRadians(this.owner.body.angle)))
            return state - delta
        } else {
            job.complete()
            return state
        }
    }
}