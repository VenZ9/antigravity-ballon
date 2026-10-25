package com.venz29.antigravityballon.core.models

enum class PowerUpType(val title: String, val durationSeconds: Float, val cost: Int) {
    SHIELD("Star Shield", 12f, 45),
    DOUBLE_COINS("2x Multiplier", 30f, 30)
}

data class FloatingEffect(
    val id: Long,
    val text: String,
    val x: Float,
    val y: Float,
    val maxDuration: Float = 0.8f,
    val elapsed: Float = 0f,
    val colorHex: Long = 0xFFFFD700
) {
    val progress: Float get() = (elapsed / maxDuration).coerceIn(0f, 1f)
    val alpha: Float get() = (1f - progress).coerceIn(0f, 1f)
    val currentY: Float get() = y - progress * 0.05f
    val isExpired: Boolean get() = elapsed >= maxDuration
}
