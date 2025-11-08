package labutils.robot

import labutils.math.Point
import labutils.math.Vector
import java.awt.Color
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin

/** The body of a [Robot]. */
data class RobotBody(
    /** The coordinates of the [Robot]. */
    var position: Point,
    /** The direction faced by the [Robot] in degrees. */
    var angle: Double = 0.0,
    /** The size of the body of the [Robot]. */
    val bodyRadius: Double = 40.0,
    /** The color of the body of the [Robot]. */
    val bodyColor: Color = Color.BLUE,
    /** The size of the front of the body of the [Robot]. */
    val frontRadius: Double = 10.0,
    /** The color of the front of the body of the [Robot]. */
    val frontColor: Color = Color.WHITE,
){
    /**
     * Convert an absolute destination [Point] into relative
     * direction [Vector]. The relative direction describes
     * exactly how much the robot should rotate and move
     * forward to reach the specified destination. Notably,
     * the angle is relative to the front of the [Robot].
     *
     * Complementary of [absolutePosition].
     *
     * @param destination the specified absolute destination [Point].
     * @return the relative direction [Vector].
     *
     * @sample Docs.exampleRelativeDirection
     */
    fun relativeDirection(destination: Point): Vector {
        val absoluteDirection = this.position.direction(destination)
        var relativeDirection = (((absoluteDirection.angle - this.angle) % 360) + 360) % 360
        if (relativeDirection > 180) { relativeDirection -= 360 }
        return Vector(absoluteDirection.length, relativeDirection)
    }
    /**
     * Convert a relative direction [Vector] into an absolute
     * destination [Point] in the environment.
     *
     * Complementary of [relativeDirection].
     *
     * @param direction the specified relative direction [Vector].
     * @return the absolute destination [Point].
     *
     * @sample Docs.exampleAbsolutePosition
     */
    fun absolutePosition(direction: Vector): Point {
        val absoluteX = this.position.x + (direction.length * cos(toRadians(direction.angle + this.angle)))
        val absoluteY = this.position.y - (direction.length * sin(toRadians(direction.angle + this.angle)))
        return Point(absoluteX, absoluteY)
    }

    private object Docs {
        private suspend fun exampleRelativeDirection() {
            val robot = Robot(RobotBody(Point(0.0, 0.0), 0.0))
            val destination = Point(10.0, 10.0)
            val relativeDirection = robot.body.relativeDirection(destination)
            robot.motor.forward(relativeDirection.length)
            robot.spinMotor.rotate(relativeDirection.angle)
        }
        private suspend fun exampleAbsolutePosition() {
            val robot = Robot(RobotBody(Point(0.0, 0.0), 0.0))
            val lightRelativeDirection = robot.lightSensor.closestLight()!!.direction
            val lightAbsolutePosition = robot.body.absolutePosition(lightRelativeDirection)
        }
    }
}