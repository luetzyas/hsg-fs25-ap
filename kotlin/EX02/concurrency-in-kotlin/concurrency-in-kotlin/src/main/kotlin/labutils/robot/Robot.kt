package labutils.robot

import labutils.environment.Environment
import labutils.robot.actuators.Led
import labutils.robot.actuators.Motor
import labutils.robot.actuators.SpinMotor
import labutils.robot.sensors.LightSensor
import labutils.simulator.Tickable
import java.awt.Color

/** The behavior of a [Robot]. */
typealias RobotBehavior = suspend (Robot) -> Unit

/** A [Robot] in the simulation. */
data class Robot(
    /** The [RobotBody] of this [Robot]. */
    val body: RobotBody,
    /** A [LightSensor] that perceives the light closest to this robot. */
    val lightSensor: LightSensor = LightSensor(radius = 10.0),
    /** A [Motor] that can move this robot forward. */
    val motor: Motor = Motor(speed = 1.0),
    /** A [SpinMotor] that can rotate this robot clockwise or counter-clockwise. */
    val spinMotor: SpinMotor = SpinMotor(speed = 1.0),
    /** A colored [Led] that can be switched on or off. */
    val led: Led = Led(radius = 10.0),
) : Tickable() {
    init {
        this.led.owner = this
        this.lightSensor.owner = this
        this.motor.owner = this
        this.spinMotor.owner = this
    }

    /**
     * Make this [Robot] behave with the specified [RobotBehavior].
     * @param behavior the specified [RobotBehavior].
     */
    suspend fun enter(behavior: RobotBehavior) { behavior(this) }

    override suspend fun tick(environment: Environment, tick: Tick) {
        this.motor.tick(environment, tick)
        this.spinMotor.tick(environment, tick)
        this.lightSensor.tick(environment, tick)
        this.led.tick(environment, tick)
    }
}