package com.venz29.antigravityballon.core

import com.venz29.antigravityballon.core.engine.CollisionDetector
import com.venz29.antigravityballon.core.models.*
import org.junit.Assert.*
import org.junit.Test

class CollisionDetectorTest {

    @Test
    fun testCircleOverlap() {
        val h1 = Hitbox(100f, 100f, 30f)
        val h2 = Hitbox(140f, 100f, 20f) // distance 40, sum radii 50 -> overlaps
        val h3 = Hitbox(200f, 100f, 20f) // distance 100, sum radii 50 -> does not overlap

        assertTrue(CollisionDetector.checkCircleOverlap(h1, h2))
        assertFalse(CollisionDetector.checkCircleOverlap(h1, h3))
    }

    @Test
    fun testObstacleCollisionDetection() {
        val screenW = 400f
        val screenH = 800f
        val scale = 1.0f

        val player = Position(0.5f, 0.5f) // px = 200, py = 400
        val directHit = Obstacle(1, 0.5f, 0.5f, 40f, HazardType.SPIKE, 0f)
        val farMiss = Obstacle(2, 0.1f, 0.1f, 40f, HazardType.SPIKE, 0f)

        val hit = CollisionDetector.findCollidingObstacle(
            player, listOf(directHit, farMiss), screenW, screenH, scale
        )

        assertNotNull(hit)
        assertEquals(1L, hit?.id)

        val hitEmpty = CollisionDetector.findCollidingObstacle(
            player, listOf(farMiss), screenW, screenH, scale
        )
        assertNull(hitEmpty)
    }

    @Test
    fun testGenerousCoinCollectionRadius() {
        val screenW = 400f
        val screenH = 800f
        val scale = 1.0f

        val player = Position(0.5f, 0.5f) // px = 200, py = 400
        // Near coin within generous pickup margin
        val nearCoin = Coin(10, 0.5f, 0.55f, 0f) // py diff = 40px (< 48px + 24px)
        val farCoin = Coin(20, 0.1f, 0.1f, 0f)

        val collected = CollisionDetector.findCollectedCoin(
            player, listOf(nearCoin, farCoin), screenW, screenH, scale
        )

        assertNotNull(collected)
        assertEquals(10L, collected?.id)
    }
}
