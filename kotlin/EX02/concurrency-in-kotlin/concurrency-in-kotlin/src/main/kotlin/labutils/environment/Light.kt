package labutils.environment

import labutils.math.Point
import java.awt.Color

/** A [Light] source in the simulation. */
data class Light(
    /** The coordinates of this [Light]. */
    var position: Point,
    /** The radius of this [Light]. */
    val radius: Double = 80.0,
    /** The color of this [Light]. */
    val color: Color = Color.YELLOW
)
