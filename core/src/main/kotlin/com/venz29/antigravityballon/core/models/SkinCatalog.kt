package com.venz29.antigravityballon.core.models

object SkinCatalog {
    val ALL_SKINS: List<BalloonSkin> = listOf(
        BalloonSkin(
            id = "ruby_classic",
            name = "Ruby Classic",
            rarity = Rarity.COMMON,
            primaryColorHex = 0xFFFF5A79,
            secondaryColorHex = 0xFFE02B52,
            accentColorHex = 0xFFFFFFFF,
            pattern = SkinPattern.CLASSIC_SWIRL,
            description = "The timeless high-flying balloon known across every sky.",
            isDefault = true
        ),
        BalloonSkin(
            id = "mint_breeze",
            name = "Mint Breeze",
            rarity = Rarity.COMMON,
            primaryColorHex = 0xFF78E0C0,
            secondaryColorHex = 0xFF189B84,
            accentColorHex = 0xFFE0FFF8,
            pattern = SkinPattern.STRIPES,
            description = "Light, fresh, and perfectly aerodynamic."
        ),
        BalloonSkin(
            id = "citrus_burst",
            name = "Citrus Burst",
            rarity = Rarity.COMMON,
            primaryColorHex = 0xFFFFE066,
            secondaryColorHex = 0xFFFFAA00,
            accentColorHex = 0xFFFFF6D0,
            pattern = SkinPattern.POLKA_DOTS,
            description = "Zesty sunshine energy in the high clouds."
        ),
        BalloonSkin(
            id = "sky_azure",
            name = "Sky Azure",
            rarity = Rarity.COMMON,
            primaryColorHex = 0xFF69C8F4,
            secondaryColorHex = 0xFF2A8BC4,
            accentColorHex = 0xFFFFFFFF,
            pattern = SkinPattern.STRIPES,
            description = "Blends harmoniously into the endless azure sky."
        ),
        BalloonSkin(
            id = "sunset_glow",
            name = "Sunset Glow",
            rarity = Rarity.RARE,
            primaryColorHex = 0xFFFF934F,
            secondaryColorHex = 0xFFEB5757,
            accentColorHex = 0xFFFFD166,
            pattern = SkinPattern.CLASSIC_SWIRL,
            description = "Bathed in the warm twilight of golden hour."
        ),
        BalloonSkin(
            id = "violet_dream",
            name = "Violet Dream",
            rarity = Rarity.RARE,
            primaryColorHex = 0xFFB388FF,
            secondaryColorHex = 0xFF7C4DFF,
            accentColorHex = 0xFFEDE7F6,
            pattern = SkinPattern.STARLIGHT,
            description = "Soft lavender hues speckled with twinkling sky dust."
        ),
        BalloonSkin(
            id = "arctic_frost",
            name = "Arctic Frost",
            rarity = Rarity.RARE,
            primaryColorHex = 0xFFE0F7FA,
            secondaryColorHex = 0xFF4DD0E1,
            accentColorHex = 0xFFFFFFFF,
            pattern = SkinPattern.STRIPES,
            description = "Cooled by alpine winds with crystal clear reflections."
        ),
        BalloonSkin(
            id = "emerald_gem",
            name = "Emerald Gem",
            rarity = Rarity.RARE,
            primaryColorHex = 0xFF66BB6A,
            secondaryColorHex = 0xFF2E7D32,
            accentColorHex = 0xFFC8E6C9,
            pattern = SkinPattern.POLKA_DOTS,
            description = "Deep forest vitality and good fortune."
        ),
        BalloonSkin(
            id = "cosmic_nebula",
            name = "Cosmic Nebula",
            rarity = Rarity.EPIC,
            primaryColorHex = 0xFF7E57C2,
            secondaryColorHex = 0xFF311B92,
            accentColorHex = 0xFFE040FB,
            pattern = SkinPattern.STARLIGHT,
            description = "Forged in the outer stratosphere with deep galactic dust."
        ),
        BalloonSkin(
            id = "solar_flare",
            name = "Solar Flare",
            rarity = Rarity.EPIC,
            primaryColorHex = 0xFFFF5722,
            secondaryColorHex = 0xFFBF360C,
            accentColorHex = 0xFFFFEB3B,
            pattern = SkinPattern.FLAME_CREST,
            description = "Surging with fiery currents and blazing updrafts."
        ),
        BalloonSkin(
            id = "cyber_neon",
            name = "Cyber Neon",
            rarity = Rarity.EPIC,
            primaryColorHex = 0xFF00E5FF,
            secondaryColorHex = 0xFF1A237E,
            accentColorHex = 0xFFFF007F,
            pattern = SkinPattern.CYBER_GRID,
            description = "High-tech synthwave aesthetic with pulsing neon wireframes."
        ),
        BalloonSkin(
            id = "royal_celestial",
            name = "Royal Celestial",
            rarity = Rarity.LEGENDARY,
            primaryColorHex = 0xFFFFD700,
            secondaryColorHex = 0xFF4A148C,
            accentColorHex = 0xFFFFFFFF,
            pattern = SkinPattern.CELESTIAL_RINGS,
            description = "An imperial masterpiece adorned with pure gold and cosmic rings."
        )
    )

    val DEFAULT_SKIN: BalloonSkin = ALL_SKINS.first { it.isDefault }

    fun findById(id: String): BalloonSkin =
        ALL_SKINS.firstOrNull { it.id == id } ?: DEFAULT_SKIN
}
