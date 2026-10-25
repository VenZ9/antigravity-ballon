package com.venz29.antigravityballon.core.models

data class Coin(
    val id: Long,
    val x: Float,
    val y: Float,
    val phase: Float,
    val collected: Boolean = false
) {
    fun update(dt: Float, speed: Float, elapsed: Float): Coin {
        val newY = y + speed * 0.88f * dt
        val sway = (kotlin.math.sin(elapsed * 2.2f + phase) * 0.015f * dt).toFloat()
        val newX = (x + sway).coerceIn(0.06f, 0.94f)
        return copy(x = newX, y = newY)
    }

    fun isOffScreen(): Boolean = y > 1.2f

    fun getHitbox(screenWidth: Float, screenHeight: Float, scale: Float): Hitbox {
        val cx = x * screenWidth
        val cy = y * screenHeight
        val radius = 24f * scale
        return Hitbox(cx, cy, radius)
    }
}
