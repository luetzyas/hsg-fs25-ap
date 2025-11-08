package labutils.robot

import labutils.simulator.Tickable

/** A [RobotComponent] that has a reference to the owner [Robot]. */
abstract class RobotComponent : Tickable() {
    /** The [Robot] owning this [RobotComponent]. */
    lateinit var owner: Robot
}