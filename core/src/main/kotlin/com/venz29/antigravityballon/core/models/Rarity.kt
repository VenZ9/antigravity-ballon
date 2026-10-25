package com.venz29.antigravityballon.core.models

enum class Rarity(
    val displayName: String,
    val weight: Int,
    val duplicateRefund: Int,
    val badgeColorHex: Long
) {
    COMMON("Common", 50, 25, 0xFF4A90E2),
    RARE("Rare", 35, 30, 0xFF9B51E0),
    EPIC("Epic", 12, 40, 0xFFFF7B00),
    LEGENDARY("Legendary", 3, 50, 0xFFFFD700)
}
