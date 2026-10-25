package com.venz29.antigravityballon.core.engine

import com.venz29.antigravityballon.core.models.Coin
import com.venz29.antigravityballon.core.models.Hitbox
import com.venz29.antigravityballon.core.models.Obstacle
import com.venz29.antigravityballon.core.models.Position
import kotlin.math.hypot

object CollisionDetector {

    fun getPlayerHitbox(
        playerPos: Position,
        screenWidth: Float,
        screenHeight: Float,
        worldScale: Float
    ): Hitbox {
        val px = playerPos.x * screenWidth
        val py = playerPos.y * screenHeight
        // Fair hitbox: slightly smaller than visual radius (42f) for player satisfaction
        val radius = 30f * worldScale
        return Hitbox(px, py, radius)
    }

    fun checkCircleOverlap(a: Hitbox, b: Hitbox): Boolean {
        val dist = hypot(a.centerX - b.centerX, a.centerY - b.centerY)
        return dist < (a.radius + b.radius)
    }

    fun findCollidingObstacle(
        playerPos: Position,
        obstacles: List<Obstacle>,
        screenWidth: Float,
        screenHeight: Float,
        worldScale: Float
    ): Obstacle? {
        val playerHitbox = getPlayerHitbox(playerPos, screenWidth, screenHeight, worldScale)
        return obstacles.firstOrNull { obs ->
            val obsHitbox = obs.getHitbox(screenWidth, screenHeight, worldScale)
            checkCircleOverlap(playerHitbox, obsHitbox)
        }
    }

    fun findCollectedCoin(
        playerPos: Position,
        coins: List<Coin>,
        screenWidth: Float,
        screenHeight: Float,
        worldScale: Float
    ): Coin? {
        val playerHitbox = getPlayerHitbox(playerPos, screenWidth, screenHeight, worldScale)
        // Generous pickup radius for satisfying coin collection
        val pickupHitbox = Hitbox(playerHitbox.centerX, playerHitbox.centerY, playerHitbox.radius + 18f * worldScale)
        return coins.firstOrNull { coin ->
            val coinHitbox = coin.getHitbox(screenWidth, screenHeight, worldScale)
            checkCircleOverlap(pickupHitbox, coinHitbox)
        }
    }
}
