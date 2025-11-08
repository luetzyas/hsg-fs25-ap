package labutils.robot.sensors

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import labutils.environment.Environment

/** A [Sensor] whose perceptions are modelled as [CompletableJob]s. */
abstract class JobSensor<I>: Sensor<I>() {
    private val mutex: Mutex = Mutex()
    private var senseJob: CompletableJob? = null
    private var senseJobResult: I? = null

    override fun lastSensed(): I? = this.senseJobResult

    override suspend fun sense(): I? {
        this.mutex.withLock { if (this.senseJob?.isCompleted != false) { this.senseJob = Job() } }
        this.senseJob?.join()
        return senseJobResult
    }

    override suspend fun tick(environment: Environment, tick: Tick) {
        this.senseJobResult = this.tickSense(environment, tick)
        this.mutex.withLock { if (this.senseJob?.isCompleted == false) { this.senseJob!!.complete() } }
    }

    /**
     * Generate the next perception of this [Sensor].
     *
     * @param environment the environment of the simulation.
     * @param tick the tick that triggered the next perception of this [Sensor].
     *
     * @return the new perception of this [Sensor].
     */
    protected abstract suspend fun tickSense(environment: Environment, tick: Tick): I?
}