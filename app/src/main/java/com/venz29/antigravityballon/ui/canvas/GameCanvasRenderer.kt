package com.venz29.antigravityballon.ui.canvas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale as scaleTransform
import androidx.compose.ui.graphics.drawscope.withTransform
import com.venz29.antigravityballon.core.models.*
import kotlin.math.*

object GameCanvasRenderer {

    fun drawSky(drawScope: DrawScope, width: Float, height: Float, skyTime: Float) {
        with(drawScope) {
            // Sky gradient
            val skyBrush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF5ABEF0),
                    Color(0xFF98DCFF),
                    Color(0xFFD6F3FF),
                    Color(0xFFF0FBFF)
                )
            )
            drawRect(skyBrush)

            // Sun with atmospheric corona
            val sunCenter = Offset(width * 0.82f, height * 0.15f)
            drawCircle(Color(0xFFFFE89A).copy(alpha = 0.28f), width * 0.13f, sunCenter)
            drawCircle(Color(0xFFFFF0B8).copy(alpha = 0.50f), width * 0.08f, sunCenter)
            drawCircle(Color(0xFFFFFBE6), width * 0.05f, sunCenter)

            // Layer 1: High background clouds (slow parallax)
            for (i in 0..4) {
                val cx = ((i * 0.28f + (skyTime * 0.015f)) % 1.3f) * width - width * 0.15f
                val cy = height * (0.12f + (i % 3) * 0.22f)
                drawPuffyCloud(cx, cy, 1.1f + (i % 2) * 0.4f, Color.White.copy(alpha = 0.35f))
            }

            // Layer 2: Mid atmosphere clouds
            for (i in 0..3) {
                val cx = ((i * 0.35f + 0.15f + (skyTime * 0.035f)) % 1.4f) * width - width * 0.2f
                val cy = height * (0.35f + (i % 3) * 0.18f)
                drawPuffyCloud(cx, cy, 1.4f, Color.White.copy(alpha = 0.52f))
            }
        }
    }

    private fun DrawScope.drawPuffyCloud(x: Float, y: Float, scale: Float, color: Color) {
        val w = 80f * scale
        val h = 26f * scale
        drawOval(color, Offset(x - w / 2, y - h / 2), Size(w, h))
        drawCircle(color, 18f * scale, Offset(x - 20f * scale, y - 7f * scale))
        drawCircle(color, 24f * scale, Offset(x + 4f * scale, y - 11f * scale))
        drawCircle(color, 17f * scale, Offset(x + 26f * scale, y - 4f * scale))
    }

    fun drawBalloon(
        drawScope: DrawScope,
        x: Float,
        y: Float,
        scale: Float,
        tilt: Float,
        skin: BalloonSkin,
        isBlinking: Boolean,
        hasShield: Boolean,
        time: Float
    ) {
        if (isBlinking) return
        with(drawScope) {
            withTransform({ rotate(tilt, Offset(x, y)) }) {
                val r = 40f * scale
                val primary = Color(skin.primaryColorHex)
                val secondary = Color(skin.secondaryColorHex)
                val accent = Color(skin.accentColorHex)

                // Ropes connecting basket to balloon
                val ropeColor = Color(0xFFB5704E)
                val ropeWidth = 2.4f * scale
                drawLine(ropeColor, Offset(x - 12f * scale, y + r * 0.9f), Offset(x - 8f * scale, y + r * 1.6f), ropeWidth)
                drawLine(ropeColor, Offset(x + 12f * scale, y + r * 0.9f), Offset(x + 8f * scale, y + r * 1.6f), ropeWidth)

                // Wicker basket
                val basketRect = Offset(x - 10f * scale, y + r * 1.6f)
                val basketSize = Size(20f * scale, 15f * scale)
                drawRoundRect(Color(0xFFD48952), basketRect, basketSize, CornerRadius(3f * scale))
                drawRoundRect(Color(0xFF9E5D2E), basketRect, basketSize, CornerRadius(3f * scale), style = Stroke(1.5f * scale))

                // Balloon neck (cone triangle)
                val neckPath = Path().apply {
                    moveTo(x - 8f * scale, y + r * 0.75f)
                    lineTo(x, y + r * 1.05f)
                    lineTo(x + 8f * scale, y + r * 0.75f)
                    close()
                }
                drawPath(neckPath, secondary)

                // Balloon main body (oval envelope)
                val bodyOffset = Offset(x - r, y - r)
                val bodySize = Size(r * 2f, r * 2.1f)
                val bodyBrush = Brush.verticalGradient(
                    colors = listOf(primary, secondary),
                    startY = y - r,
                    endY = y + r * 1.05f
                )
                drawOval(bodyBrush, bodyOffset, bodySize)

                // Skin Patterns
                when (skin.pattern) {
                    SkinPattern.STRIPES -> {
                        val stripePath = Path().apply {
                            moveTo(x - 10f * scale, y - r * 0.9f)
                            lineTo(x + 10f * scale, y - r * 0.9f)
                            lineTo(x + 5f * scale, y + r * 0.85f)
                            lineTo(x - 5f * scale, y + r * 0.85f)
                            close()
                        }
                        drawPath(stripePath, accent.copy(alpha = 0.55f))
                    }
                    SkinPattern.POLKA_DOTS -> {
                        drawCircle(accent.copy(alpha = 0.65f), 4.5f * scale, Offset(x - 14f * scale, y - 5f * scale))
                        drawCircle(accent.copy(alpha = 0.65f), 5.5f * scale, Offset(x, y - 10f * scale))
                        drawCircle(accent.copy(alpha = 0.65f), 4.5f * scale, Offset(x + 14f * scale, y - 5f * scale))
                        drawCircle(accent.copy(alpha = 0.65f), 4.0f * scale, Offset(x - 7f * scale, y + 14f * scale))
                        drawCircle(accent.copy(alpha = 0.65f), 4.0f * scale, Offset(x + 7f * scale, y + 14f * scale))
                    }
                    SkinPattern.STARLIGHT -> {
                        for (i in 0..4) {
                            val angle = i * (PI * 2 / 5).toFloat()
                            val dist = 14f * scale
                            val sx = x + cos(angle) * dist
                            val sy = y + sin(angle) * dist
                            drawCircle(accent.copy(alpha = 0.75f), 2.8f * scale, Offset(sx, sy))
                        }
                        drawCircle(accent, 4f * scale, Offset(x, y - 4f * scale))
                    }
                    SkinPattern.FLAME_CREST -> {
                        val flame = Path().apply {
                            moveTo(x, y - r * 0.8f)
                            quadraticTo(x + 12f * scale, y, x + 6f * scale, y + r * 0.6f)
                            quadraticTo(x, y + 8f * scale, x - 6f * scale, y + r * 0.6f)
                            quadraticTo(x - 12f * scale, y, x, y - r * 0.8f)
                            close()
                        }
                        drawPath(flame, accent.copy(alpha = 0.65f))
                    }
                    SkinPattern.CYBER_GRID -> {
                        drawLine(accent.copy(alpha = 0.7f), Offset(x - r * 0.8f, y - 6f * scale), Offset(x + r * 0.8f, y - 6f * scale), 2f * scale)
                        drawLine(accent.copy(alpha = 0.7f), Offset(x - r * 0.7f, y + 10f * scale), Offset(x + r * 0.7f, y + 10f * scale), 2f * scale)
                        drawLine(accent.copy(alpha = 0.7f), Offset(x, y - r * 0.85f), Offset(x, y + r * 0.85f), 2f * scale)
                    }
                    SkinPattern.CELESTIAL_RINGS -> {
                        drawOval(
                            accent.copy(alpha = 0.75f),
                            Offset(x - r * 1.25f, y - 8f * scale),
                            Size(r * 2.5f, 16f * scale),
                            style = Stroke(3f * scale)
                        )
                    }
                    SkinPattern.CLASSIC_SWIRL -> {
                        // Aerodynamic highlight rib
                        drawOval(accent.copy(alpha = 0.35f), Offset(x - r * 0.55f, y - r * 0.55f), Size(r * 0.32f, r * 0.75f))
                    }
                }

                // 3D glossy highlights
                drawOval(Color.White.copy(alpha = 0.50f), Offset(x - r * 0.52f, y - r * 0.55f), Size(r * 0.28f, r * 0.72f))
                drawCircle(Color.White.copy(alpha = 0.40f), r * 0.10f, Offset(x + r * 0.46f, y - r * 0.30f))

                // Active Shield Aura
                if (hasShield) {
                    val pulse = 1f + sin(time * 8f).toFloat() * 0.05f
                    val shieldRadius = 58f * scale * pulse
                    drawCircle(Color(0xFF62E4FF).copy(alpha = 0.22f), shieldRadius, Offset(x, y))
                    drawCircle(Color(0xFF88F2FF).copy(alpha = 0.85f), shieldRadius, Offset(x, y), style = Stroke(3.5f * scale))
                }
            }
        }
    }

    fun drawObstacle(
        drawScope: DrawScope,
        obstacle: Obstacle,
        width: Float,
        height: Float,
        scale: Float,
        time: Float
    ) {
        val cx = obstacle.x * width
        val cy = obstacle.y * height
        with(drawScope) {
            withTransform({ scaleTransform(scale = scale, pivot = Offset(cx, cy)) }) {
                when (obstacle.type) {
                    HazardType.CLOUD -> {
                        // Storm cloud with dark base
                        drawPuffyCloud(cx, cy + 2f, obstacle.size / 70f, Color(0xFF90B4C8))
                        drawPuffyCloud(cx, cy, obstacle.size / 70f, Color(0xFFE4F0F6))
                        // Storm eyes
                        drawCircle(Color(0xFF385E70), 2.2f, Offset(cx - 8f, cy + 1f))
                        drawCircle(Color(0xFF385E70), 2.2f, Offset(cx + 8f, cy + 1f))
                    }
                    HazardType.BIRD -> {
                        // Flapping wings
                        val flap = sin(time * 12f + obstacle.phase).toFloat() * 12f
                        val wingPath = Path().apply {
                            moveTo(cx - 20f, cy + flap * 0.5f)
                            quadraticTo(cx - 10f, cy - 14f + flap, cx, cy)
                            quadraticTo(cx + 10f, cy - 14f + flap, cx + 20f, cy + flap * 0.5f)
                            quadraticTo(cx + 10f, cy + 6f, cx, cy + 3f)
                            quadraticTo(cx - 10f, cy + 6f, cx - 20f, cy + flap * 0.5f)
                            close()
                        }
                        drawPath(wingPath, Color(0xFF425C88))
                        drawCircle(Color.White, 3f, Offset(cx + 8f, cy - 2f))
                        drawCircle(Color(0xFF1E2F48), 1.5f, Offset(cx + 9f, cy - 2f))
                        // Beak
                        val beak = Path().apply {
                            moveTo(cx + 19f, cy - 2f)
                            lineTo(cx + 27f, cy + 1f)
                            lineTo(cx + 19f, cy + 4f)
                            close()
                        }
                        drawPath(beak, Color(0xFFFFAE33))
                    }
                    HazardType.SPIKE -> {
                        // Crystal spike mine
                        val half = obstacle.size * 0.5f
                        rotate(time * 45f + obstacle.phase, Offset(cx, cy)) {
                            val spike = Path().apply {
                                moveTo(cx, cy - half)
                                lineTo(cx + half, cy + half)
                                lineTo(cx - half, cy + half)
                                close()
                            }
                            drawPath(spike, Color(0xFF8B55C6))
                            drawPath(spike, Brush.linearGradient(listOf(Color(0xFFDFC6FF), Color(0xFF8049BE))), style = androidx.compose.ui.graphics.drawscope.Fill)
                            drawLine(Color.White.copy(alpha = 0.7f), Offset(cx - 8f, cy + 8f), Offset(cx, cy - 16f), 2.5f)
                        }
                    }
                    HazardType.STAR -> {
                        // Spinning hazard star
                        rotate(time * 75f + obstacle.phase, Offset(cx, cy)) {
                            val path = Path()
                            for (i in 0 until 10) {
                                val a = -PI / 2 + i * PI / 5
                                val r = if (i % 2 == 0) obstacle.size * 0.5f else obstacle.size * 0.25f
                                val px = cx + cos(a).toFloat() * r
                                val py = cy + sin(a).toFloat() * r
                                if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                            }
                            path.close()
                            drawPath(path, Color(0xFFFF9500))
                            drawCircle(Color(0xFFFFD466), obstacle.size * 0.16f, Offset(cx, cy))
                        }
                    }
                    HazardType.COMET -> {
                        // Solar comet with tail
                        val tail = Path().apply {
                            moveTo(cx - 9f, cy + 5f)
                            lineTo(cx + 6f, cy + 7f)
                            lineTo(cx + 25f, cy + 36f)
                            lineTo(cx + 15f, cy + 32f)
                            close()
                        }
                        drawPath(tail, Brush.verticalGradient(listOf(Color(0xFFFFDE6A), Color(0xFFFF523B)), cy, cy + 36f))
                        drawCircle(Color(0xFFE53935), 14f, Offset(cx, cy))
                        drawCircle(Color(0xFFFFA726), 9f, Offset(cx - 3f, cy - 3f))
                        drawCircle(Color(0xFFFFF9C4), 4f, Offset(cx - 5f, cy - 5f))
                    }
                    HazardType.LIGHTNING -> {
                        // Electric lightning bolt
                        val bolt = Path().apply {
                            moveTo(cx + 4f, cy - obstacle.size * 0.5f)
                            lineTo(cx - 12f, cy)
                            lineTo(cx - 2f, cy)
                            lineTo(cx - 8f, cy + obstacle.size * 0.5f)
                            lineTo(cx + 14f, cy - 7f)
                            lineTo(cx + 4f, cy - 7f)
                            close()
                        }
                        drawPath(bolt, Color(0xFF2979FF))
                        drawPath(bolt, Color(0xFFE1F5FE), style = Stroke(2.5f))
                    }
                    HazardType.DRONE -> {
                        // Aerial scout drone
                        rotate(sin(time * 8f + obstacle.phase).toFloat() * 18f, Offset(cx, cy)) {
                            drawRoundRect(Color(0xFF455A64), Offset(cx - 24f, cy - 5f), Size(48f, 10f), CornerRadius(4f))
                            drawCircle(Color(0xFF78909C), 8f, Offset(cx - 24f, cy))
                            drawCircle(Color(0xFF78909C), 8f, Offset(cx + 24f, cy))
                            drawCircle(Color(0xFFFF5252), 4.5f, Offset(cx, cy))
                            // Rotors
                            drawLine(Color(0xFF263238), Offset(cx - 30f, cy - 8f), Offset(cx - 18f, cy - 8f), 2f)
                            drawLine(Color(0xFF263238), Offset(cx + 18f, cy - 8f), Offset(cx + 30f, cy - 8f), 2f)
                        }
                    }
                }
            }
        }
    }

    fun drawCoin(
        drawScope: DrawScope,
        coin: Coin,
        width: Float,
        height: Float,
        scale: Float,
        time: Float
    ) {
        val cx = coin.x * width
        val cy = coin.y * height
        val spin = abs(cos(time * 5.5f + coin.phase)).coerceAtLeast(0.18f)
        with(drawScope) {
            withTransform({ scaleTransform(scale = scale, pivot = Offset(cx, cy)) }) {
                // Outer glow
                drawCircle(Color(0xFFFFC107).copy(alpha = 0.25f), 22f, Offset(cx, cy))
                // Coin face
                val ovalOffset = Offset(cx - 12f * spin, cy - 15f)
                val ovalSize = Size(24f * spin, 30f)
                drawOval(
                    Brush.verticalGradient(listOf(Color(0xFFFFE082), Color(0xFFFFB300))),
                    ovalOffset,
                    ovalSize
                )
                // Inner rim
                drawOval(
                    Color(0xFFFFF8E1),
                    Offset(cx - 7f * spin, cy - 10f),
                    Size(14f * spin, 20f),
                    style = Stroke(1.8f)
                )
                // Sparkle
                drawCircle(Color.White, 2f, Offset(cx - 3f * spin, cy - 4f))
            }
        }
    }

    fun drawCoinBurst(
        drawScope: DrawScope,
        x: Float,
        y: Float,
        scale: Float,
        flashTimer: Float
    ) {
        val progress = 1f - (flashTimer / 0.35f).coerceIn(0f, 1f)
        val alpha = (flashTimer / 0.35f).coerceIn(0f, 1f)
        with(drawScope) {
            drawCircle(
                Color(0xFFFFD54F).copy(alpha = alpha * 0.7f),
                (24f + progress * 38f) * scale,
                Offset(x, y),
                style = Stroke((4f - progress * 2.5f) * scale)
            )
            for (i in 0 until 8) {
                val angle = i * (PI / 4).toFloat() + progress * 0.5f
                val dist = (20f + progress * 40f) * scale
                val px = x + cos(angle) * dist
                val py = y + sin(angle) * dist
                drawCircle(Color(0xFFFFB300).copy(alpha = alpha), 3f * scale, Offset(px, py))
            }
        }
    }
}
