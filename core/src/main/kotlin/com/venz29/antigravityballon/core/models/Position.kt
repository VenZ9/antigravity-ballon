package com.venz29.antigravityballon.core.models

data class Position(val x: Float, val y: Float) {
    fun offset(dx: Float, dy: Float): Position = Position(x + dx, y + dy)
    fun clamped(minX: Float, maxX: Float, minY: Float, maxY: Float): Position =
        Position(x.coerceIn(minX, maxX), y.coerceIn(minY, maxY))
}

data class Velocity(val vx: Float, val vy: Float)

data class Hitbox(val centerX: Float, val centerY: Float, val radius: Float)
