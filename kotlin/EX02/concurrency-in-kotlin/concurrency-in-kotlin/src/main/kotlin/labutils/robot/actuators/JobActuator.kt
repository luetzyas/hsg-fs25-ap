package labutils.robot.actuators

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import labutils.environment.Environment

/** An [Actuator] whose tasks are modelled as [CompletableJob]s. */
abstract class JobActuator<I>: Actuator<I>() {
    private val mutex: Mutex = Mutex()
    private var actJob: CompletableJob? = null
    private var actJobState: I? = null

    override suspend fun act(input: I) {
        this.mutex.withLock {
            if (this.actJob?.isCompleted != false) { this.actJob = Job() }
            this.actJobState = input
        }
        this.actJob?.join()
    }

    override suspend fun tick(environment: Environment, tick: Tick) {
        this.mutex.withLock {
            if (this.actJob?.isCompleted == false) {
                this.actJobState = this.tickJob(this.actJob!!, this.actJobState!!, environment, tick)
            }
        }
    }

    /**
     * Progress the specified [CompletableJob] with the specified current job state [I].
     * The job may be completed after an arbitrary amounts of ticks.
     *
     * @param job the specified [CompletableJob].
     * @param state the specified current job state.
     * @param environment the environment of the simulation.
     * @param tick the tick that triggered this progress on the job.
     *
     * @return the new state for the job.
     */
    protected abstract suspend fun tickJob(job: CompletableJob, state: I, environment: Environment, tick: Tick): I
}