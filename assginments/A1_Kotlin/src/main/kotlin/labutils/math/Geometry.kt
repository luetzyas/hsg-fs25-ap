package labutils.math

import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/** A [Point] identifying a position in two dimensions. */
data class Point(
    /** The horizontal coordinate of this [Point]. */
    var x: Double,
    /** The vertical coordinate of this [Point]. */
    var y: Double
){
    /** @return the horizontal distance with the specified [Point]. */
    fun dx(other: Point): Double = this.x - other.x

    /** @return the vertical distance with the specified [Point]. */
    fun dy(other: Point): Double = this.y - other.y

    /** @return the square distance with the specified [Point]. */
    fun squareDistance(other: Point): Double {
        val dx = this.dx(other)
        val dy = this.dy(other)
        return dx * dx + dy * dy
    }

    /** @return the distance with the specified [Point]. */
    fun distance(other: Point): Double =
        sqrt(squareDistance(other))

    /** @return the absolute direction with the specified [Point]. */
    fun direction(other: Point): Vector =
        Vector(
            angle = ((toDegrees(atan2(this.dy(other), -this.dx(other))) % 360) + 360) % 360,
            length = this.distance(other)
        )
}

data class Vector(val length: Double, val angle: Double)