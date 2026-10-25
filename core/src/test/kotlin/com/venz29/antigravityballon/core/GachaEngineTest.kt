package com.venz29.antigravityballon.core

import com.venz29.antigravityballon.core.engine.GachaEngine
import com.venz29.antigravityballon.core.engine.GachaResult
import com.venz29.antigravityballon.core.models.Rarity
import com.venz29.antigravityballon.core.models.SkinCatalog
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class GachaEngineTest {

    @Test
    fun testInsufficientCoinsFailsGracefully() {
        val engine = GachaEngine()
        val result = engine.pull(currentCoins = 30, ownedSkinIds = setOf("ruby_classic"))

        assertTrue("Expected InsufficientCoins result", result is GachaResult.InsufficientCoins)
        val failure = result as GachaResult.InsufficientCoins
        assertEquals(50, failure.required)
        assertEquals(30, failure.available)
        assertEquals(20, failure.shortfall)
    }

    @Test
    fun testSuccessfulPullDeductsCoinsAndAwardsSkin() {
        // Deterministic random seed
        val engine = GachaEngine(Random(12345))
        val initialCoins = 100
        val result = engine.pull(currentCoins = initialCoins, ownedSkinIds = setOf("ruby_classic"))

        assertTrue("Expected Success result", result is GachaResult.Success)
        val success = result as GachaResult.Success
        assertNotNull(success.skin)
        assertTrue(SkinCatalog.ALL_SKINS.contains(success.skin))
    }

    @Test
    fun testDuplicateSkinAwardsFairCoinRefund() {
        // Own all skins so every pull is guaranteed duplicate
        val allSkinIds = SkinCatalog.ALL_SKINS.map { it.id }.toSet()
        val engine = GachaEngine(Random(42))

        val initialCoins = 100
        val result = engine.pull(currentCoins = initialCoins, ownedSkinIds = allSkinIds)

        assertTrue(result is GachaResult.Success)
        val success = result as GachaResult.Success
        assertTrue("Must be marked as duplicate", success.isDuplicate)
        assertTrue("Refund must be positive", success.refundCoins > 0)
        assertEquals(success.skin.rarity.duplicateRefund, success.refundCoins)
        // Final balance = 100 - 50 + refund
        assertEquals(50 + success.refundCoins, success.newBalance)
    }

    @Test
    fun testDeterministicSeedProducesReproduciblePulls() {
        val seed = 987654321L
        val engine1 = GachaEngine(Random(seed))
        val engine2 = GachaEngine(Random(seed))

        val result1 = engine1.pull(100, emptySet()) as GachaResult.Success
        val result2 = engine2.pull(100, emptySet()) as GachaResult.Success

        assertEquals(result1.skin.id, result2.skin.id)
        assertEquals(result1.skin.rarity, result2.skin.rarity)
        assertEquals(result1.refundCoins, result2.refundCoins)
    }

    @Test
    fun testRarityDistribution() {
        val engine = GachaEngine(Random(777))
        val counts = mutableMapOf<Rarity, Int>()
        val totalRolls = 1000

        for (i in 0 until totalRolls) {
            val result = engine.pull(100, emptySet()) as GachaResult.Success
            counts[result.skin.rarity] = (counts[result.skin.rarity] ?: 0) + 1
        }

        // Common (~50%) should be the highest
        val commonCount = counts[Rarity.COMMON] ?: 0
        val rareCount = counts[Rarity.RARE] ?: 0
        val epicCount = counts[Rarity.EPIC] ?: 0

        assertTrue("Common should have highest occurrences (~500)", commonCount in 400..600)
        assertTrue("Rare should have substantial occurrences (~350)", rareCount in 250..450)
        assertTrue("Epic should have smaller occurrences (~120)", epicCount in 60..200)
    }
}
