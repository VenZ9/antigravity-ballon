package com.venz29.antigravityballon.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venz29.antigravityballon.core.engine.GachaEngine
import com.venz29.antigravityballon.core.engine.GachaResult
import com.venz29.antigravityballon.core.models.BalloonSkin
import com.venz29.antigravityballon.core.models.Rarity
import com.venz29.antigravityballon.core.models.SkinCatalog
import com.venz29.antigravityballon.ui.canvas.GameCanvasRenderer
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GachaScreen(
    wallet: Int,
    unlockedSkinIds: Set<String>,
    time: Float,
    onPullTriggered: () -> GachaResult,
    onSkinEquipped: (String) -> Unit,
    onBackClicked: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val wheelRotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var lastResult by remember { mutableStateOf<GachaResult.Success?>(null) }
    var showResultDialog by remember { mutableStateOf(false) }

    val canAfford = wallet >= GachaEngine.PULL_COST
    val shortfall = (GachaEngine.PULL_COST - wallet).coerceAtLeast(0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClicked,
                modifier = Modifier.background(Color.White.copy(alpha = 0.85f), CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF1B4E63)
                )
            }

            Text(
                text = "SKY GACHA",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1B4E63),
                letterSpacing = 2.sp
            )

            CoinBadge(wallet)
        }

        Spacer(Modifier.height(16.dp))

        // Gacha Machine Centerpiece
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(32.dp))
                .background(Color.White.copy(alpha = 0.94f), RoundedCornerShape(32.dp))
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "LUCKY CAPSULE WHEEL",
                color = Color(0xFF1B4E63),
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                letterSpacing = 1.sp
            )
            Text(
                text = "Spin for original collectible balloon skins!",
                color = Color(0xFF5A8B9E),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
            )

            // Animated Lucky Wheel
            Box(
                modifier = Modifier.size(170.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .size(154.dp)
                        .graphicsLayer { rotationZ = wheelRotation.value }
                ) {
                    val count = SkinCatalog.ALL_SKINS.size
                    val sweep = 360f / count
                    SkinCatalog.ALL_SKINS.forEachIndexed { index, skin ->
                        val startAngle = index * sweep
                        drawArc(
                            color = Color(skin.secondaryColorHex),
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = true
                        )
                        val rad = Math.toRadians((startAngle + sweep / 2.0)).toFloat()
                        val dotDist = size.minDimension * 0.44f
                        val dotX = center.x + cos(rad) * dotDist
                        val dotY = center.y + sin(rad) * dotDist
                        drawCircle(Color.White.copy(alpha = 0.85f), 3.5f, Offset(dotX, dotY))
                    }
                    drawCircle(Color(0xFFFFF9E6), size.minDimension * 0.22f)
                    drawCircle(Color(0xFFFFC107), size.minDimension * 0.16f)
                }

                Text("✦", color = Color(0xFF664600), fontSize = 26.sp, fontWeight = FontWeight.Black)

                // Pointer
                Text(
                    text = "▼",
                    color = Color(0xFFFF3366),
                    fontSize = 24.sp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-4).dp)
                )
            }

            Spacer(Modifier.height(18.dp))

            // Pull Button
            Button(
                onClick = {
                    if (canAfford && !isSpinning) {
                        isSpinning = true
                        val result = onPullTriggered()
                        if (result is GachaResult.Success) {
                            lastResult = result
                            coroutineScope.launch {
                                val target = wheelRotation.value + 1800f + (0..360).random()
                                wheelRotation.animateTo(
                                    targetValue = target,
                                    animationSpec = tween(durationMillis = 2200, easing = FastOutSlowInEasing)
                                )
                                isSpinning = false
                                showResultDialog = true
                            }
                        } else {
                            isSpinning = false
                        }
                    }
                },
                enabled = canAfford && !isSpinning,
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC107),
                    disabledContainerColor = Color(0xFFE0E0E0)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                val label = when {
                    isSpinning -> "SPINNING…"
                    canAfford -> "PULL SKIN  ·  50 ¢"
                    else -> "NEED $shortfall MORE COINS"
                }
                Text(
                    text = label,
                    color = if (canAfford && !isSpinning) Color(0xFF664600) else Color(0xFF757575),
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Probability and Duplicate Policy Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.88f), RoundedCornerShape(22.dp))
                .padding(18.dp)
        ) {
            Text(
                text = "DROP PROBABILITIES",
                color = Color(0xFF1B4E63),
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(10.dp))

            ProbabilityRow("Common Skins", "50%", Rarity.COMMON)
            ProbabilityRow("Rare Skins", "35%", Rarity.RARE)
            ProbabilityRow("Epic Skins", "12%", Rarity.EPIC)
            ProbabilityRow("Legendary Skins", "3%", Rarity.LEGENDARY)

            Spacer(Modifier.height(12.dp))

            // Fair Duplicate Protection Policy
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "✓ Fair Duplicate Protection: Duplicates are never wasted! Rolling an owned skin grants a fair coin refund (up to 50 coins).",
                    color = Color(0xFF2E7D32),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 15.sp
                )
            }
        }
    }

    // Result Reveal Dialog
    if (showResultDialog && lastResult != null) {
        val result = lastResult!!
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(28.dp),
            title = {
                Text(
                    text = if (result.isDuplicate) "DUPLICATE REWARD!" else "★ NEW SKIN UNLOCKED! ★",
                    fontWeight = FontWeight.Black,
                    color = if (result.isDuplicate) Color(0xFFE65100) else Color(0xFF2E7D32),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            GameCanvasRenderer.drawBalloon(
                                drawScope = this,
                                x = size.width / 2f,
                                y = size.height / 2f,
                                scale = 1.1f,
                                tilt = 0f,
                                skin = result.skin,
                                isBlinking = false,
                                hasShield = false,
                                time = time
                            )
                        }
                    }

                    Text(
                        text = result.skin.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1B4E63)
                    )

                    Spacer(Modifier.height(4.dp))
                    RarityBadge(result.skin.rarity)

                    Spacer(Modifier.height(8.dp))
                    if (result.isDuplicate) {
                        Text(
                            text = "You already own this skin! You received +${result.refundCoins} coins refund.",
                            color = Color(0xFFE65100),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = result.skin.description,
                            color = Color(0xFF5A8B9E),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            confirmButton = {
                if (!result.isDuplicate) {
                    Button(
                        onClick = {
                            onSkinEquipped(result.skin.id)
                            showResultDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("EQUIP NOW", color = Color(0xFF664600), fontWeight = FontWeight.Black)
                    }
                } else {
                    Button(
                        onClick = { showResultDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("COLLECT REFUND", color = Color(0xFF664600), fontWeight = FontWeight.Black)
                    }
                }
            },
            dismissButton = {
                if (!result.isDuplicate) {
                    TextButton(onClick = { showResultDialog = false }) {
                        Text("CLOSE", color = Color(0xFF1B4E63), fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
    }
}

@Composable
private fun ProbabilityRow(name: String, percentage: String, rarity: Rarity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color(rarity.badgeColorHex), CircleShape)
            )
            Spacer(Modifier.width(8.dp))
            Text(name, color = Color(0xFF37474F), fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        Text(percentage, color = Color(0xFF1B4E63), fontSize = 13.sp, fontWeight = FontWeight.Black)
    }
}
