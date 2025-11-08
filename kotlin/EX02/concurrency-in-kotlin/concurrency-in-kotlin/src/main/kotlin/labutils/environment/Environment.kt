package labutils.environment

import labutils.robot.Robot

/** The [Environment] containing all entities in the simulation. */
data class Environment(val configuration: EnvironmentConfiguration) {
    private val robots: MutableSet<Robot> = mutableSetOf()
    private val lights: MutableSet<Light> = mutableSetOf()

    /** @return a shallow snapshot of the [Robot]s in the simulation. */
    fun robots(): Set<Robot> = HashSet(this.robots)
    /** @return a shallow snapshot of the [Light]s in the simulation. */
    fun lights(): Set<Light> = HashSet(this.lights)

    /**
     * Add the specified [Robot]s to this [Environment].
     * @param robots the specified [Robot]s.
     * @return this
     */
    @Synchronized fun withRobots(vararg robots: Robot): Environment {
        this.robots.addAll(robots)
        return this
    }
    /**
     * Remove the specified [Robot]s to this [Environment].
     * @param robots the specified [Robot]s.
     * @return this
     */
    @Synchronized fun withoutRobots(vararg robots: Robot): Environment {
        this.robots.removeAll(robots.toSet())
        return this
    }
    /**
     * Add the specified [Light]s to this [Environment].
     * @param lights the specified [Robot]s.
     * @return this
     */
    @Synchronized fun withLights(vararg lights: Light): Environment {
        this.lights.addAll(lights)
        return this
    }
    /**
     * Remove the specified [Light]s to this [Environment].
     * @param lights the specified [Light]s.
     * @return this
     */
    @Synchronized fun withoutLights(vararg lights: Light): Environment {
        this.lights.removeAll(lights.toSet())
        return this
    }
}
