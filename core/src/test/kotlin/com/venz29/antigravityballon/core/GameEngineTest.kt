package com.venz29.antigravityballon.core

import com.venz29.antigravityballon.core.engine.GameEngine
import com.venz29.antigravityballon.core.engine.GameState
import com.venz29.antigravityballon.core.models.HazardType
import com.venz29.antigravityballon.core.models.Obstacle
import org.junit.Assert.*
import org.junit.Test

class GameEngineTest {

    @Test
    fun testInitialRunState() {
        val engine = GameEngine()
        engine.startRun()

        assertEquals(GameState.PLAYING, engine.state)
        assertEquals(3, engine.lives)
        assertEquals(0, engine.score)
        assertEquals(0, engine.runCoins)
        assertFalse(engine.isInvulnerable)
        assertFalse(engine.hasShield)
        assertFalse(engine.isDoubleCoins)
    }

    @Test
    fun testTiltTrackingAndRecovery() {
        val engine = GameEngine()
        engine.startRun()

        // Drag right
        engine.handleDrag(0.05f, 0f)
        assertTrue("Target tilt should tilt right", engine.targetTilt > 0f)

        // Update dt to smooth tilt
        engine.update(0.1f, 400f, 800f, 1f)
        assertTrue("Tilt should smoothly increase towards target tilt", engine.tilt > 0f)

        // End drag -> target tilt goes to 0
        engine.handleDragEnd()
        assertEquals(0f, engine.targetTilt, 0.001f)

        // Multiple updates smoothly decay tilt back towards 0
        for (i in 0 until 10) {
            engine.update(0.05f, 400f, 800f, 1f)
        }
        assertTrue("Tilt should decay close to 0", engine.tilt < 5f)
    }

    @Test
    fun testLivesReductionAndInvulnerability() {
        val engine = GameEngine()
        engine.startRun()

        var collisionCount = 0
        engine.onCollisionHit = { _, _ -> collisionCount++ }

        // Spawns obstacle on top of player (0.5, 0.65)
        val dummyObstacle = Obstacle(999, 0.5f, 0.65f, 40f, HazardType.SPIKE, 0f)

        // Manually place obstacle by reflection or triggering update
        // We can test collision via update with obstacle directly in the engine
        val obstacleField = GameEngine::class.java.getDeclaredField("obstacles")
        obstacleField.isAccessible = true
        obstacleField.set(engine, listOf(dummyObstacle))

        engine.update(0.016f, 400f, 800f, 1f)

        assertEquals(1, collisionCount)
        assertEquals(2, engine.lives)
        assertTrue(engine.isInvulnerable)

        // Subsequent update with another obstacle during invulnerability must not reduce life
        val anotherObstacle = Obstacle(1000, 0.5f, 0.65f, 40f, HazardType.CLOUD, 0f)
        obstacleField.set(engine, listOf(anotherObstacle))
        engine.update(0.016f, 400f, 800f, 1f)

        assertEquals(2, engine.lives)
        assertEquals(1, collisionCount)
    }

    @Test
    fun testGameOverWhenLivesDepleted() {
        val engine = GameEngine()
        engine.startRun()

        var gameOverTriggered = false
        engine.onGameOver = { _, _ -> gameOverTriggered = true }

        val livesField = GameEngine::class.java.getDeclaredField("lives")
        livesField.isAccessible = true
        livesField.set(engine, 1)

        val obstacleField = GameEngine::class.java.getDeclaredField("obstacles")
        obstacleField.isAccessible = true
        obstacleField.set(engine, listOf(Obstacle(888, 0.5f, 0.65f, 40f, HazardType.COMET, 0f)))

        engine.update(0.016f, 400f, 800f, 1f)

        assertEquals(0, engine.lives)
        assertEquals(GameState.GAME_OVER, engine.state)
        assertTrue(gameOverTriggered)
    }

    @Test
    fun testShieldAbsorbsCollision() {
        val engine = GameEngine()
        engine.startRun(hasShieldBoost = true)

        assertTrue(engine.hasShield)
        var shieldAbsorbed = false
        engine.onShieldAbsorb = { shieldAbsorbed = true }

        val obstacleField = GameEngine::class.java.getDeclaredField("obstacles")
        obstacleField.isAccessible = true
        obstacleField.set(engine, listOf(Obstacle(555, 0.5f, 0.65f, 40f, HazardType.DRONE, 0f)))

        engine.update(0.016f, 400f, 800f, 1f)

        assertTrue(shieldAbsorbed)
        assertEquals(3, engine.lives) // Lives kept safe!
        assertFalse(engine.hasShield) // Shield used up
    }
}
