package com.venz29.antigravityballon.core.models

data class BalloonSkin(
    val id: String,
    val name: String,
    val rarity: Rarity,
    val primaryColorHex: Long,
    val secondaryColorHex: Long,
    val accentColorHex: Long,
    val pattern: SkinPattern,
    val description: String,
    val isDefault: Boolean = false
)
