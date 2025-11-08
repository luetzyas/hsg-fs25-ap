package labutils.environment

/** A configuration for an [Environment]. */
data class EnvironmentConfiguration(
    /** The width of the [Environment]. */
    val width: Double = 800.0,
    /** The height of the [Environment]. */
    val height: Double = 600.0,
    /** The scale of the [Environment]. */
    val scale: Double = 1.0,
)
