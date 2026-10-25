package com.venz29.antigravityballon.core.models

data class Obstacle(
    val id: Long,
    val x: Float,
    val y: Float,
    val size: Float,
    val type: HazardType,
    val phase: Float,
    val baseX: Float = x
) {
    fun update(dt: Float, speed: Float, elapsed: Float): Obstacle {
        val newY = y + speed * dt
        val sway = if (type.swayAmplitude > 0f) {
            (kotlin.math.sin(elapsed * type.swayFrequency + phase) * type.swayAmplitude * dt).toFloat()
        } else 0f
        val newX = (x + sway).coerceIn(0.06f, 0.94f)
        return copy(x = newX, y = newY)
    }

    fun isOffScreen(): Boolean = y > 1.25f

    fun getHitbox(screenWidth: Float, screenHeight: Float, scale: Float): Hitbox {
        val cx = x * screenWidth
        val cy = y * screenHeight
        val radius = size * type.collisionFactor * scale
        return Hitbox(cx, cy, radius)
    }
}
