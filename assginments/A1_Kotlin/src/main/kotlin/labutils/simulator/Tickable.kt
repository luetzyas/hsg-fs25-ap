package labutils.simulator

import labutils.environment.Environment

/** An entity that changes depending on discrete time. */
abstract class Tickable {
    /** A packet of discrete time. */
    class Tick(dt: Int)

    /**
     * Change this entity because of time passing.
     * @param environment the current environment containing the entity.
     * @param tick the packet of discrete time passed.
     */
    open suspend fun tick(environment: Environment, tick: Tick) {}
}