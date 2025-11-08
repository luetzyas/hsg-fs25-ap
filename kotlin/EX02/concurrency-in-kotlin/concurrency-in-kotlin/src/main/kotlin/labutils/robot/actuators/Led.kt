package labutils.robot.actuators

import kotlinx.coroutines.CompletableJob
import labutils.environment.Environment
import java.awt.Color

/** A colored [Led] that can be switched on or off. */
data class Led(
    /** The size of this [Led]. */
    val radius: Double,
    private var color: Color = Color.WHITE,
    private var isLit: Boolean = false
) : JobActuator<Led.Change>() {
    class Change(val isLit: Boolean, val newColor: Color)

    /**
     * @return true if this [Led] is lit; false otherwise.
     */
    fun isLit(): Boolean = this.isLit

    /**
     * @return the color of this [Led].
     */
    fun color(): Color = this.color

    /**
     * Set up a task to turn this [Led] on or off depending on the
     * specified flag. Optionally, a [Color] can be specified to
     * change the color of this [Led].
     *
     * @param on the specified flag.
     * @param color the specified color.
     */
    suspend fun switch(on: Boolean, color: Color? = null) =
        this.act(Change(isLit = on, newColor = color ?: this.color))

    override suspend fun tickJob(job: CompletableJob, state: Change, environment: Environment, tick: Tick): Change {
        this.isLit = state.isLit
        this.color = state.newColor
        job.complete()
        return state
    }
}