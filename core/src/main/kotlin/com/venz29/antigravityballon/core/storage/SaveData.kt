package com.venz29.antigravityballon.core.storage

import com.venz29.antigravityballon.core.models.SkinCatalog

data class SaveData(
    val totalCoins: Int = 0,
    val bestScore: Int = 0,
    val unlockedSkinIds: Set<String> = setOf(SkinCatalog.DEFAULT_SKIN.id),
    val equippedSkinId: String = SkinCatalog.DEFAULT_SKIN.id,
    val musicEnabled: Boolean = true,
    val sfxEnabled: Boolean = true,
    val highSensitivity: Boolean = false,
    val doubleCoinsOwned: Boolean = false,
    val shieldOwned: Boolean = false,
    val runsPlayed: Int = 0
) {
    fun validated(): SaveData {
        val safeCoins = totalCoins.coerceAtLeast(0)
        val safeBest = bestScore.coerceAtLeast(0)
        val safeSkins = if (unlockedSkinIds.isEmpty()) {
            setOf(SkinCatalog.DEFAULT_SKIN.id)
        } else {
            unlockedSkinIds + SkinCatalog.DEFAULT_SKIN.id
        }
        val safeEquipped = if (equippedSkinId in safeSkins) {
            equippedSkinId
        } else {
            SkinCatalog.DEFAULT_SKIN.id
        }

        return copy(
            totalCoins = safeCoins,
            bestScore = safeBest,
            unlockedSkinIds = safeSkins,
            equippedSkinId = safeEquipped,
            runsPlayed = runsPlayed.coerceAtLeast(0)
        )
    }

    fun withCoinsAdded(amount: Int): SaveData =
        copy(totalCoins = (totalCoins + amount).coerceAtLeast(0))

    fun withCoinsDeducted(amount: Int): SaveData =
        copy(totalCoins = (totalCoins - amount).coerceAtLeast(0))

    fun withNewScore(score: Int): SaveData =
        copy(bestScore = maxOf(bestScore, score))

    fun withUnlockedSkin(skinId: String): SaveData =
        copy(unlockedSkinIds = unlockedSkinIds + skinId)

    fun withEquippedSkin(skinId: String): SaveData =
        if (skinId in unlockedSkinIds) copy(equippedSkinId = skinId) else this
}
