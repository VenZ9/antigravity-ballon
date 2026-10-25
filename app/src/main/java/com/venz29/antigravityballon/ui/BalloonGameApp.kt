package com.venz29.antigravityballon.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.venz29.antigravityballon.audio.AudioController
import com.venz29.antigravityballon.core.engine.GachaEngine
import com.venz29.antigravityballon.core.engine.GachaResult
import com.venz29.antigravityballon.core.engine.GameEngine
import com.venz29.antigravityballon.core.engine.GameState
import com.venz29.antigravityballon.core.models.PowerUpType
import com.venz29.antigravityballon.core.models.SkinCatalog
import com.venz29.antigravityballon.storage.PreferencesSaveRepository
import com.venz29.antigravityballon.ui.canvas.GameCanvasRenderer
import com.venz29.antigravityballon.ui.screens.*
import kotlinx.coroutines.isActive
import kotlin.math.min
import kotlin.math.sin

enum class AppScreen {
    MENU,
    PLAYING,
    WARDROBE,
    GACHA
}

@Composable
fun BalloonGameApp() {
    val context = LocalContext.current
    val saveRepo = remember { PreferencesSaveRepository(context) }
    var saveData by remember { mutableStateOf(saveRepo.load()) }

    val audio = remember {
        AudioController(context).apply {
            musicEnabled = saveData.musicEnabled
            sfxEnabled = saveData.sfxEnabled
        }
    }
    DisposableEffect(audio) {
        onDispose { audio.release() }
    }

    val engine = remember { GameEngine() }
    val gachaEngine = remember { GachaEngine() }

    var currentScreen by remember { mutableStateOf(AppScreen.MENU) }
    var showSettings by remember { mutableStateOf(false) }
    var showPauseMenu by remember { mutableStateOf(false) }
    var showGameOverMenu by remember { mutableStateOf(false) }
    var isNewRecord by remember { mutableStateOf(false) }

    var skyTime by remember { mutableFloatStateOf(0f) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    // Start background music when app is opened
    LaunchedEffect(saveData.musicEnabled) {
        if (saveData.musicEnabled) {
            audio.startMusic()
        } else {
            audio.pauseMusic()
        }
    }

    // Connect game engine callbacks
    LaunchedEffect(engine) {
        engine.onCoinCollected = { _, _ ->
            audio.playCoin()
        }
        engine.onCollisionHit = { _, livesLeft ->
            audio.playBump()
        }
        engine.onShieldAbsorb = {
            audio.playBump()
        }
        engine.onGameOver = { finalScore, coinsEarned ->
            audio.playGameOver()
            val isRecord = finalScore > saveData.bestScore
            isNewRecord = isRecord

            val updated = saveData
                .withCoinsAdded(coinsEarned)
                .withNewScore(finalScore)
                .copy(runsPlayed = saveData.runsPlayed + 1)
            saveData = updated
            saveRepo.save(updated)

            showGameOverMenu = true
        }
    }

    // High performance continuous game loop
    LaunchedEffect(currentScreen) {
        var lastNanos = 0L
        while (isActive) {
            withFrameNanos { now ->
                val dt = if (lastNanos == 0L) 0f else ((now - lastNanos) / 1_000_000_000f).coerceIn(0f, 0.05f)
                lastNanos = now
                skyTime += dt

                if (currentScreen == AppScreen.PLAYING && canvasSize.width > 0f) {
                    val scale = (min(canvasSize.width, canvasSize.height) / 360f).coerceIn(0.8f, 3.5f)
                    engine.update(dt, canvasSize.width, canvasSize.height, scale)
                }
            }
        }
    }

    val equippedSkin = remember(saveData.equippedSkinId) {
        SkinCatalog.findById(saveData.equippedSkinId)
    }

    val sensitivity = if (saveData.highSensitivity) 1.35f else 1.0f

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Full Screen Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(currentScreen) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                if (currentScreen == AppScreen.PLAYING && size.width > 0 && size.height > 0) {
                                    val dxNorm = dragAmount.x / size.width
                                    val dyNorm = dragAmount.y / size.height
                                    engine.handleDrag(dxNorm, dyNorm, sensitivity)
                                }
                            },
                            onDragEnd = {
                                if (currentScreen == AppScreen.PLAYING) {
                                    engine.handleDragEnd()
                                }
                            },
                            onDragCancel = {
                                if (currentScreen == AppScreen.PLAYING) {
                                    engine.handleDragEnd()
                                }
                            }
                        )
                    }
            ) {
                canvasSize = this.size
                val scale = (min(size.width, size.height) / 360f).coerceIn(0.8f, 3.5f)

                // 1. Endless Sky
                GameCanvasRenderer.drawSky(this, size.width, size.height, skyTime)

                // 2. Gameplay elements
                if (currentScreen == AppScreen.PLAYING) {
                    // Obstacles
                    engine.obstacles.forEach { obs ->
                        GameCanvasRenderer.drawObstacle(this, obs, size.width, size.height, scale, skyTime)
                    }

                    // Coins
                    engine.coins.forEach { coin ->
                        GameCanvasRenderer.drawCoin(this, coin, size.width, size.height, scale, skyTime)
                    }

                    // Balloon
                    val isBlinking = engine.isInvulnerable && ((skyTime * 14).toInt() % 2 == 0)
                    val bx = engine.player.x * size.width
                    val by = engine.player.y * size.height + sin(skyTime * 3.5f).toFloat() * 4f * scale
                    GameCanvasRenderer.drawBalloon(
                        drawScope = this,
                        x = bx,
                        y = by,
                        scale = scale,
                        tilt = engine.tilt,
                        skin = equippedSkin,
                        isBlinking = isBlinking,
                        hasShield = engine.hasShield,
                        time = skyTime
                    )

                    // Coin burst sparkles
                    if (engine.coinFlashTimer > 0f) {
                        GameCanvasRenderer.drawCoinBurst(this, bx, by, scale, engine.coinFlashTimer)
                    }

                    // Collision red hit flash
                    if (engine.hitFlashTimer > 0f) {
                        val alpha = (engine.hitFlashTimer / 0.30f * 0.22f).coerceIn(0f, 0.25f)
                        drawRect(Color(0xFFFF3366).copy(alpha = alpha))
                    }
                } else if (currentScreen == AppScreen.MENU) {
                    // Menu idle floating balloon
                    val mx = size.width * 0.5f
                    val my = size.height * 0.52f + sin(skyTime * 2.4f).toFloat() * 8f * scale
                    val mtilt = sin(skyTime * 1.6f).toFloat() * 8f
                    GameCanvasRenderer.drawBalloon(
                        drawScope = this,
                        x = mx,
                        y = my,
                        scale = scale * 1.25f,
                        tilt = mtilt,
                        skin = equippedSkin,
                        isBlinking = false,
                        hasShield = false,
                        time = skyTime
                    )
                }
            }

            // Top HUD when playing
            if (currentScreen == AppScreen.PLAYING) {
                GamePlayHUD(
                    engine = engine,
                    onPauseClicked = {
                        audio.playClick()
                        engine.pause()
                        showPauseMenu = true
                    }
                )
            }

            // Main Menu Screen
            if (currentScreen == AppScreen.MENU) {
                MainMenuScreen(
                    wallet = saveData.totalCoins,
                    bestScore = saveData.bestScore,
                    time = skyTime,
                    shieldReady = saveData.shieldOwned,
                    doubleCoinsReady = saveData.doubleCoinsOwned,
                    onPlayClicked = {
                        audio.playClick()
                        val shieldBoost = saveData.shieldOwned
                        val doubleCoinsBoost = saveData.doubleCoinsOwned
                        if (shieldBoost || doubleCoinsBoost) {
                            val updated = saveData.copy(shieldOwned = false, doubleCoinsOwned = false)
                            saveData = updated
                            saveRepo.save(updated)
                        }
                        engine.startRun(hasShieldBoost = shieldBoost, hasDoubleCoinsBoost = doubleCoinsBoost)
                        currentScreen = AppScreen.PLAYING
                        showGameOverMenu = false
                        showPauseMenu = false
                    },
                    onWardrobeClicked = {
                        audio.playClick()
                        currentScreen = AppScreen.WARDROBE
                    },
                    onGachaClicked = {
                        audio.playClick()
                        currentScreen = AppScreen.GACHA
                    },
                    onSettingsClicked = {
                        audio.playClick()
                        showSettings = true
                    },
                    onBuyBoost = { boostType ->
                        if (saveData.totalCoins >= boostType.cost) {
                            audio.playClick()
                            val updated = when (boostType) {
                                PowerUpType.SHIELD -> saveData.withCoinsDeducted(boostType.cost).copy(shieldOwned = true)
                                PowerUpType.DOUBLE_COINS -> saveData.withCoinsDeducted(boostType.cost).copy(doubleCoinsOwned = true)
                            }
                            saveData = updated
                            saveRepo.save(updated)
                        }
                    }
                )
            }

            // Wardrobe Screen
            if (currentScreen == AppScreen.WARDROBE) {
                SkinCollectionScreen(
                    wallet = saveData.totalCoins,
                    unlockedSkinIds = saveData.unlockedSkinIds,
                    equippedSkinId = saveData.equippedSkinId,
                    time = skyTime,
                    onSkinEquipped = { skinId ->
                        audio.playClick()
                        val updated = saveData.withEquippedSkin(skinId)
                        saveData = updated
                        saveRepo.save(updated)
                    },
                    onBackClicked = {
                        audio.playClick()
                        currentScreen = AppScreen.MENU
                    }
                )
            }

            // Sky Gacha Screen
            if (currentScreen == AppScreen.GACHA) {
                GachaScreen(
                    wallet = saveData.totalCoins,
                    unlockedSkinIds = saveData.unlockedSkinIds,
                    time = skyTime,
                    onPullTriggered = {
                        val result = gachaEngine.pull(saveData.totalCoins, saveData.unlockedSkinIds)
                        if (result is GachaResult.Success) {
                            if (!result.isDuplicate) {
                                audio.playUnlock()
                                val updated = saveData
                                    .withCoinsDeducted(GachaEngine.PULL_COST)
                                    .withUnlockedSkin(result.skin.id)
                                saveData = updated
                                saveRepo.save(updated)
                            } else {
                                audio.playCoin()
                                val updated = saveData
                                    .withCoinsDeducted(GachaEngine.PULL_COST)
                                    .withCoinsAdded(result.refundCoins)
                                saveData = updated
                                saveRepo.save(updated)
                            }
                        }
                        result
                    },
                    onSkinEquipped = { skinId ->
                        audio.playClick()
                        val updated = saveData.withEquippedSkin(skinId)
                        saveData = updated
                        saveRepo.save(updated)
                    },
                    onBackClicked = {
                        audio.playClick()
                        currentScreen = AppScreen.MENU
                    }
                )
            }

            // Pause Dialog
            if (showPauseMenu) {
                PauseDialog(
                    onResumeClicked = {
                        audio.playClick()
                        engine.resume()
                        showPauseMenu = false
                    },
                    onRestartClicked = {
                        audio.playClick()
                        showPauseMenu = false
                        engine.startRun()
                    },
                    onMainMenuClicked = {
                        audio.playClick()
                        showPauseMenu = false
                        currentScreen = AppScreen.MENU
                    },
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Game Over Dialog
            if (showGameOverMenu) {
                GameOverDialog(
                    score = engine.score,
                    bestScore = saveData.bestScore,
                    runCoins = engine.runCoins,
                    totalCoins = saveData.totalCoins,
                    isNewRecord = isNewRecord,
                    onRestartClicked = {
                        audio.playClick()
                        showGameOverMenu = false
                        engine.startRun()
                    },
                    onMainMenuClicked = {
                        audio.playClick()
                        showGameOverMenu = false
                        currentScreen = AppScreen.MENU
                    },
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Settings Dialog
            if (showSettings) {
                SettingsDialog(
                    musicEnabled = saveData.musicEnabled,
                    sfxEnabled = saveData.sfxEnabled,
                    highSensitivity = saveData.highSensitivity,
                    onMusicToggled = { enabled ->
                        audio.musicEnabled = enabled
                        val updated = saveData.copy(musicEnabled = enabled)
                        saveData = updated
                        saveRepo.save(updated)
                    },
                    onSfxToggled = { enabled ->
                        audio.sfxEnabled = enabled
                        val updated = saveData.copy(sfxEnabled = enabled)
                        saveData = updated
                        saveRepo.save(updated)
                    },
                    onSensitivityToggled = { enabled ->
                        val updated = saveData.copy(highSensitivity = enabled)
                        saveData = updated
                        saveRepo.save(updated)
                    },
                    onResetProgress = {
                        saveRepo.clear()
                        saveData = saveRepo.load()
                        audio.musicEnabled = saveData.musicEnabled
                        audio.sfxEnabled = saveData.sfxEnabled
                    },
                    onDismiss = {
                        audio.playClick()
                        showSettings = false
                    }
                )
            }
        }
    }
}
