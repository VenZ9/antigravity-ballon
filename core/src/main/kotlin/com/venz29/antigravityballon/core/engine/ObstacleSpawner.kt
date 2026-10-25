package com.venz29.antigravityballon.core.engine

import com.venz29.antigravityballon.core.models.Coin
import com.venz29.antigravityballon.core.models.HazardType
import com.venz29.antigravityballon.core.models.Obstacle
import kotlin.random.Random

class ObstacleSpawner(private val random: Random = Random.Default) {

    private var nextObstacleId = 1L
    private var nextCoinId = 1L

    var obstacleTimer: Float = 0.6f
        private set
    var coinTimer: Float = 1.2f
        private set

    fun reset() {
        obstacleTimer = 0.6f
        coinTimer = 1.2f
    }

    fun update(
        dt: Float,
        difficulty: Float,
        onSpawnObstacles: (List<Obstacle>) -> Unit,
        onSpawnCoins: (List<Coin>) -> Unit
    ) {
        obstacleTimer -= dt
        coinTimer -= dt

        if (obstacleTimer <= 0f) {
            val spawned = spawnWave(difficulty)
            onSpawnObstacles(spawned)
            // Interval scales smoothly from 1.35s down to 0.62s
            val baseInterval = (1.35f - difficulty * 0.70f).coerceAtLeast(0.62f)
            obstacleTimer = baseInterval + random.nextFloat() * 0.40f
        }

        if (coinTimer <= 0f) {
            val coins = spawnCoinTrail()
            onSpawnCoins(coins)
            coinTimer = 1.1f + random.nextFloat() * 0.8f
        }
    }

    private fun spawnWave(difficulty: Float): List<Obstacle> {
        val count = if (difficulty > 0.65f && random.nextFloat() < 0.35f) 2 else 1
        val results = mutableListOf<Obstacle>()

        if (count == 1) {
            val type = HazardType.entries[random.nextInt(HazardType.entries.size)]
            val size = when (type) {
                HazardType.CLOUD -> 48f + random.nextFloat() * 16f
                HazardType.BIRD -> 36f
                HazardType.SPIKE -> 42f
                HazardType.STAR -> 38f
                HazardType.COMET -> 40f
                HazardType.LIGHTNING -> 44f
                HazardType.DRONE -> 42f
            }
            val x = (0.08f + random.nextFloat() * 0.84f).coerceIn(0.08f, 0.92f)
            val y = -0.12f
            results.add(Obstacle(nextObstacleId++, x, y, size, type, random.nextFloat() * 6.28f))
        } else {
            // Dual hazard with guaranteed open flight corridor
            val lane1 = 0.12f + random.nextFloat() * 0.28f
            val lane2 = 0.60f + random.nextFloat() * 0.28f
            val type1 = HazardType.entries[random.nextInt(HazardType.entries.size)]
            val type2 = HazardType.entries[random.nextInt(HazardType.entries.size)]
            results.add(Obstacle(nextObstacleId++, lane1, -0.12f, type1.baseSize, type1, random.nextFloat() * 6.28f))
            results.add(Obstacle(nextObstacleId++, lane2, -0.18f, type2.baseSize, type2, random.nextFloat() * 6.28f))
        }

        return results
    }

    private fun spawnCoinTrail(): List<Coin> {
        val trailCount = random.nextInt(3, 6)
        val startX = 0.15f + random.nextFloat() * 0.70f
        val drift = (random.nextFloat() * 0.08f - 0.04f)
        val coins = mutableListOf<Coin>()
        for (i in 0 until trailCount) {
            val cx = (startX + drift * i).coerceIn(0.08f, 0.92f)
            val cy = -0.08f - i * 0.09f
            coins.add(Coin(nextCoinId++, cx, cy, random.nextFloat() * 6.28f))
        }
        return coins
    }
}
