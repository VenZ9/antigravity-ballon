package com.venz29.antigravityballon.core.engine

import com.venz29.antigravityballon.core.models.BalloonSkin
import com.venz29.antigravityballon.core.models.Rarity
import com.venz29.antigravityballon.core.models.SkinCatalog
import kotlin.random.Random

sealed class GachaResult {
    data class Success(
        val skin: BalloonSkin,
        val isDuplicate: Boolean,
        val refundCoins: Int,
        val newBalance: Int
    ) : GachaResult()

    data class InsufficientCoins(
        val required: Int,
        val available: Int,
        val shortfall: Int
    ) : GachaResult()
}

class GachaEngine(private val random: Random = Random.Default) {

    companion object {
        const val PULL_COST: Int = 50

        val RARITY_ODDS: Map<Rarity, Float> = mapOf(
            Rarity.COMMON to 0.50f,
            Rarity.RARE to 0.35f,
            Rarity.EPIC to 0.12f,
            Rarity.LEGENDARY to 0.03f
        )
    }

    fun pull(
        currentCoins: Int,
        ownedSkinIds: Set<String>
    ): GachaResult {
        if (currentCoins < PULL_COST) {
            return GachaResult.InsufficientCoins(
                required = PULL_COST,
                available = currentCoins,
                shortfall = PULL_COST - currentCoins
            )
        }

        val balanceAfterCost = currentCoins - PULL_COST

        // Roll rarity based on weights
        val rolledRarity = rollRarity()

        // Select skin of that rarity
        val pool = SkinCatalog.ALL_SKINS.filter { it.rarity == rolledRarity }
        val selectedSkin = pool[random.nextInt(pool.size)]

        val isDuplicate = selectedSkin.id in ownedSkinIds
        val refund = if (isDuplicate) rolledRarity.duplicateRefund else 0
        val finalBalance = balanceAfterCost + refund

        return GachaResult.Success(
            skin = selectedSkin,
            isDuplicate = isDuplicate,
            refundCoins = refund,
            newBalance = finalBalance
        )
    }

    private fun rollRarity(): Rarity {
        val totalWeight = Rarity.entries.sumOf { it.weight }
        val roll = random.nextInt(totalWeight)
        var cumulative = 0
        for (rarity in Rarity.entries) {
            cumulative += rarity.weight
            if (roll < cumulative) {
                return rarity
            }
        }
        return Rarity.COMMON
    }
}
