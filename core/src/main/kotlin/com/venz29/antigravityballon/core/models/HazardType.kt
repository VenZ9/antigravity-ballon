package com.venz29.antigravityballon.core.models

enum class HazardType(
    val displayName: String,
    val baseSize: Float,
    val collisionFactor: Float,
    val swayAmplitude: Float,
    val swayFrequency: Float
) {
    CLOUD("Storm Cloud", 52f, 0.32f, 0f, 0f),
    BIRD("Sky Sparrow", 36f, 0.38f, 0.07f, 2.4f),
    SPIKE("Crystal Spike", 42f, 0.35f, 0f, 0f),
    STAR("Spun Star", 38f, 0.36f, 0.02f, 1.8f),
    COMET("Solar Comet", 40f, 0.36f, 0.045f, 1.5f),
    LIGHTNING("Electric Bolt", 44f, 0.34f, 0f, 0f),
    DRONE("Sky Scout Drone", 42f, 0.38f, 0.09f, 3.2f)
}
