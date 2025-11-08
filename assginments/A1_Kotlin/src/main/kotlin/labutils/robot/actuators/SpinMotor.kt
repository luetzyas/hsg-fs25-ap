package labutils.robot.actuators

import kotlinx.coroutines.CompletableJob
import labutils.environment.Environment
import kotlin.math.max
import kotlin.math.min

/** A [SpinMotor] that can rotate the robot clockwise or counter-clockwise with the given speed. */
data class SpinMotor(private val speed: Double) : JobActuator<Double>() {
    /**
     * Alias for [act].
     *
     * Set up a task that will rotate the owner robot of the specified
     * angle over time. Positive angles mean that the robot will turn
     * left; negative angles mean that the robot will turn right.
     *
     * @param angle the specified angle.
     */
    suspend fun rotate(angle: Double) = this.act(angle)

    override suspend fun tickJob(job: CompletableJob, state: Double, environment: Environment, tick: Tick): Double {
        if (state != 0.0) {
            val delta = if (state > 0) min(state, this.speed) else max(state, -this.speed)
            this.owner.body.angle = (((this.owner.body.angle + delta) % 360) + 360) % 360
            return state - delta
        } else {
            job.complete()
            return state
        }
    }
}