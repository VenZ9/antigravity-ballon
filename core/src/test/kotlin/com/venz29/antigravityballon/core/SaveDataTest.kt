package com.venz29.antigravityballon.core

import com.venz29.antigravityballon.core.models.SkinCatalog
import com.venz29.antigravityballon.core.storage.SaveData
import org.junit.Assert.*
import org.junit.Test

class SaveDataTest {

    @Test
    fun testDefaultSaveState() {
        val save = SaveData()
        assertEquals(0, save.totalCoins)
        assertEquals(0, save.bestScore)
        assertTrue(save.unlockedSkinIds.contains(SkinCatalog.DEFAULT_SKIN.id))
        assertEquals(SkinCatalog.DEFAULT_SKIN.id, save.equippedSkinId)
        assertTrue(save.musicEnabled)
        assertTrue(save.sfxEnabled)
    }

    @Test
    fun testCorruptedStateValidation() {
        val corrupted = SaveData(
            totalCoins = -50,
            bestScore = -10,
            unlockedSkinIds = emptySet(),
            equippedSkinId = "non_existent_skin"
        )

        val fixed = corrupted.validated()
        assertEquals(0, fixed.totalCoins)
        assertEquals(0, fixed.bestScore)
        assertTrue("Starter skin must be restored", fixed.unlockedSkinIds.contains(SkinCatalog.DEFAULT_SKIN.id))
        assertEquals("Equipped skin must fall back to default", SkinCatalog.DEFAULT_SKIN.id, fixed.equippedSkinId)
    }

    @Test
    fun testCoinTransactions() {
        val initial = SaveData(totalCoins = 100)
        val added = initial.withCoinsAdded(45)
        assertEquals(145, added.totalCoins)

        val deducted = added.withCoinsDeducted(60)
        assertEquals(85, deducted.totalCoins)

        val overDeducted = deducted.withCoinsDeducted(200)
        assertEquals(0, overDeducted.totalCoins)
    }

    @Test
    fun testSkinUnlockAndEquip() {
        val initial = SaveData()
        val unlocked = initial.withUnlockedSkin("mint_breeze")
        assertTrue(unlocked.unlockedSkinIds.contains("mint_breeze"))

        val equipped = unlocked.withEquippedSkin("mint_breeze")
        assertEquals("mint_breeze", equipped.equippedSkinId)

        // Equipping locked skin should be ignored
        val invalidEquip = equipped.withEquippedSkin("cyber_neon")
        assertEquals("mint_breeze", invalidEquip.equippedSkinId)
    }
}
