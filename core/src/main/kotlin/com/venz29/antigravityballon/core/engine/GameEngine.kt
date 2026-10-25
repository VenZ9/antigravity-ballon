package com.venz29.antigravityballon.core.engine

import com.venz29.antigravityballon.core.models.*
import kotlin.math.sin

enum class GameState {
    MENU,
    PLAYING,
    PAUSED,
    GAME_OVER
}

class GameEngine(
    private val spawner: ObstacleSpawner = ObstacleSpawner()
) {
    var state: GameState = GameState.MENU
        private set

    var player: Position = Position(0.5f, 0.65f)
        private set
    var targetPlayer: Position = Position(0.5f, 0.65f)
        private set
    var tilt: Float = 0f
        private set
    var targetTilt: Float = 0f
        private set

    var lives: Int = 3
        private set
    var score: Int = 0
        private set
    var runCoins: Int = 0
        private set
    var elapsed: Float = 0f
        private set

    var invulnerableTimer: Float = 0f
        private set
    var shieldTimer: Float = 0f
        private set
    var doubleCoinsTimer: Float = 0f
        private set
    var hitFlashTimer: Float = 0f
        private set
    var coinFlashTimer: Float = 0f
        private set

    var obstacles: List<Obstacle> = emptyList()
        private set
    var coins: List<Coin> = emptyList()
        private set
    var floatingTexts: List<FloatingEffect> = emptyList()
        private set

    private var nextTextId = 1L

    // Event callbacks
    var onCoinCollected: ((Coin, Int) -> Unit)? = null
    var onCollisionHit: ((Obstacle, Int) -> Unit)? = null
    var onShieldAbsorb: ((Obstacle) -> Unit)? = null
    var onGameOver: ((Int, Int) -> Unit)? = null

    val isInvulnerable: Boolean get() = invulnerableTimer > 0f
    val hasShield: Boolean get() = shieldTimer > 0f
    val isDoubleCoins: Boolean get() = doubleCoinsTimer > 0f

    fun startRun(hasShieldBoost: Boolean = false, hasDoubleCoinsBoost: Boolean = false) {
        state = GameState.PLAYING
        player = Position(0.5f, 0.65f)
        targetPlayer = Position(0.5f, 0.65f)
        tilt = 0f
        targetTilt = 0f
        lives = 3
        score = 0
        runCoins = 0
        elapsed = 0f
        invulnerableTimer = 0f
        hitFlashTimer = 0f
        coinFlashTimer = 0f
        obstacles = emptyList()
        coins = emptyList()
        floatingTexts = emptyList()
        spawner.reset()

        shieldTimer = if (hasShieldBoost) PowerUpType.SHIELD.durationSeconds else 0f
        doubleCoinsTimer = if (hasDoubleCoinsBoost) PowerUpType.DOUBLE_COINS.durationSeconds else 0f
    }

    fun pause() {
        if (state == GameState.PLAYING) {
            state = GameState.PAUSED
        }
    }

    fun resume() {
        if (state == GameState.PAUSED) {
            state = GameState.PLAYING
        }
    }

    fun handleDrag(dxNorm: Float, dyNorm: Float, sensitivityMultiplier: Float = 1.0f) {
        if (state != GameState.PLAYING) return
        val adjDx = dxNorm * sensitivityMultiplier
        val adjDy = dyNorm * sensitivityMultiplier
        targetPlayer = targetPlayer.offset(adjDx, adjDy).clamped(0.08f, 0.92f, 0.12f, 0.88f)
        targetTilt = (adjDx * 600f).coerceIn(-48f, 48f)
    }

    fun handleDragEnd() {
        targetTilt = 0f
    }

    fun update(
        dt: Float,
        screenWidth: Float,
        screenHeight: Float,
        worldScale: Float
    ) {
        val safeDt = dt.coerceIn(0f, 0.05f)

        // Smooth tilt recovery & tracking
        val tiltSpeed = (safeDt * 12f).coerceIn(0f, 1f)
        tilt += (targetTilt - tilt) * tiltSpeed

        // Update floating texts even if paused or playing
        floatingTexts = floatingTexts.map { it.copy(elapsed = it.elapsed + safeDt) }
            .filterNot { it.isExpired }

        if (state != GameState.PLAYING) return

        elapsed += safeDt
        score = elapsed.toInt()

        // Responsive position smoothing
        val steerSpeed = (safeDt * 14f).coerceIn(0f, 1f)
        player = Position(
            player.x + (targetPlayer.x - player.x) * steerSpeed,
            player.y + (targetPlayer.y - player.y) * steerSpeed
        )

        // Timers
        invulnerableTimer = (invulnerableTimer - safeDt).coerceAtLeast(0f)
        shieldTimer = (shieldTimer - safeDt).coerceAtLeast(0f)
        doubleCoinsTimer = (doubleCoinsTimer - safeDt).coerceAtLeast(0f)
        hitFlashTimer = (hitFlashTimer - safeDt).coerceAtLeast(0f)
        coinFlashTimer = (coinFlashTimer - safeDt).coerceAtLeast(0f)

        // Difficulty scaling: ramps from 0 to 1 over 60 seconds
        val difficulty = (elapsed / 60f).coerceAtMost(1f)
        val speed = 0.22f + difficulty * 0.12f

        // Spawning
        spawner.update(
            safeDt,
            difficulty,
            onSpawnObstacles = { newObs -> obstacles = obstacles + newObs },
            onSpawnCoins = { newCoins -> coins = coins + newCoins }
        )

        // Update obstacles & coins movement
        obstacles = obstacles.map { it.update(safeDt, speed, elapsed) }
            .filterNot { it.isOffScreen() }
        coins = coins.map { it.update(safeDt, speed, elapsed) }
            .filterNot { it.isOffScreen() }

        // Coin collision check
        val collected = CollisionDetector.findCollectedCoin(
            player, coins, screenWidth, screenHeight, worldScale
        )
        if (collected != null) {
            val amount = if (isDoubleCoins) 2 else 1
            runCoins += amount
            coinFlashTimer = 0.35f
            coins = coins.filterNot { it.id == collected.id }

            // Add floating +1 / +2 effect
            floatingTexts = floatingTexts + FloatingEffect(
                id = nextTextId++,
                text = "+$amount",
                x = player.x,
                y = player.y - 0.05f,
                colorHex = if (isDoubleCoins) 0xFFFFB300 else 0xFFFFD700
            )

            onCoinCollected?.invoke(collected, amount)
        }

        // Hazard collision check
        if (!isInvulnerable) {
            val colliding = CollisionDetector.findCollidingObstacle(
                player, obstacles, screenWidth, screenHeight, worldScale
            )
            if (colliding != null) {
                obstacles = obstacles.filterNot { it.id == colliding.id }

                if (hasShield) {
                    // Shield absorbs the hazard without damage
                    shieldTimer = 0f
                    invulnerableTimer = 0.6f
                    onShieldAbsorb?.invoke(colliding)
                } else {
                    lives -= 1
                    invulnerableTimer = 1.4f
                    hitFlashTimer = 0.30f
                    onCollisionHit?.invoke(colliding, lives)

                    if (lives <= 0) {
                        state = GameState.GAME_OVER
                        onGameOver?.invoke(score, runCoins)
                    }
                }
            }
        }
    }
}
