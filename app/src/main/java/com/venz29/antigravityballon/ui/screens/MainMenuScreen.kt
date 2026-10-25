package com.venz29.antigravityballon.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venz29.antigravityballon.R
import com.venz29.antigravityballon.core.models.PowerUpType
import kotlin.math.sin

@Composable
fun MainMenuScreen(
    wallet: Int,
    bestScore: Int,
    time: Float,
    shieldReady: Boolean,
    doubleCoinsReady: Boolean,
    onPlayClicked: () -> Unit,
    onWardrobeClicked: () -> Unit,
    onGachaClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onBuyBoost: (PowerUpType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar: Coins & Settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CoinBadge(wallet)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onGachaClicked,
                    modifier = Modifier.background(Color.White.copy(alpha = 0.85f), CircleShape)
                ) {
                    Icon(Icons.Default.Redeem, contentDescription = "Sky Gacha", tint = Color(0xFFE65100))
                }
                IconButton(
                    onClick = onWardrobeClicked,
                    modifier = Modifier.background(Color.White.copy(alpha = 0.85f), CircleShape)
                ) {
                    Icon(Icons.Default.Palette, contentDescription = "Wardrobe", tint = Color(0xFF6A1B9A))
                }
                IconButton(
                    onClick = onSettingsClicked,
                    modifier = Modifier.background(Color.White.copy(alpha = 0.85f), CircleShape)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFF235C75))
                }
            }
        }

        Spacer(Modifier.weight(0.35f))

        // Animated Logo & Title
        Image(
            painter = painterResource(R.drawable.ic_balloon_logo),
            contentDescription = "Balloon Protector Logo",
            modifier = Modifier
                .size(92.dp)
                .graphicsLayer {
                    rotationZ = sin(time * 1.8f).toFloat() * 4f
                    val scale = 1f + sin(time * 2.2f).toFloat() * 0.035f
                    scaleX = scale
                    scaleY = scale
                }
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "BALLOON",
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            letterSpacing = 4.sp,
            style = MaterialTheme.typography.displaySmall.copy(
                shadow = Shadow(Color(0x66205469), Offset(0f, 4f), 10f)
            )
        )
        Text(
            text = "PROTECTOR",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF235C75),
            letterSpacing = 8.sp
        )

        Text(
            text = "Float high, dodge hazards, collect gold!",
            fontSize = 14.sp,
            color = Color(0xFF38768F),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(Modifier.weight(0.45f))

        // Best Score Card
        Box(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(20.dp))
                .padding(horizontal = 24.dp, vertical = 10.dp)
        ) {
            Text(
                text = "BEST ALTITUDE  ·  ${bestScore}m",
                color = Color(0xFF235C75),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )
        }

        Spacer(Modifier.height(18.dp))

        // Primary Play Button
        Button(
            onClick = onPlayClicked,
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
            modifier = Modifier
                .fillMaxWidth(0.78f)
                .height(60.dp)
                .shadow(10.dp, RoundedCornerShape(28.dp))
        ) {
            Text(
                text = "LET’S FLY",
                color = Color(0xFF664600),
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontSize = 19.sp
            )
        }

        Spacer(Modifier.height(14.dp))

        // Secondary Action Row: Wardrobe & Gacha Buttons
        Row(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onWardrobeClicked,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF235C75)),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            ) {
                Text("SKINS", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
            }

            Button(
                onClick = onGachaClicked,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8F00)),
                modifier = Modifier
                    .weight(1.2f)
                    .height(48.dp)
            ) {
                Text("SKY GACHA", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Power-Up Boost Quick Row
        Row(
            modifier = Modifier.fillMaxWidth(0.92f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BoostChip(
                title = "2x COINS",
                cost = PowerUpType.DOUBLE_COINS.cost,
                isReady = doubleCoinsReady,
                canAfford = wallet >= PowerUpType.DOUBLE_COINS.cost,
                onClick = { onBuyBoost(PowerUpType.DOUBLE_COINS) },
                modifier = Modifier.weight(1f)
            )
            BoostChip(
                title = "SHIELD",
                cost = PowerUpType.SHIELD.cost,
                isReady = shieldReady,
                canAfford = wallet >= PowerUpType.SHIELD.cost,
                onClick = { onBuyBoost(PowerUpType.SHIELD) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.weight(0.2f))

        Text(
            text = "DRAG TO FLY IN ALL DIRECTIONS",
            color = Color(0xFF4C8EA8),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun CoinBadge(amount: Int) {
    Row(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.90f), RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(Color(0xFFFFC107), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("¢", color = Color(0xFF785400), fontWeight = FontWeight.Black, fontSize = 11.sp)
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = "$amount",
            color = Color(0xFF5A4000),
            fontWeight = FontWeight.Black,
            fontSize = 15.sp
        )
    }
}

@Composable
private fun BoostChip(
    title: String,
    cost: Int,
    isReady: Boolean,
    canAfford: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = !isReady && canAfford,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isReady) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.85f),
            disabledContainerColor = if (isReady) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
        modifier = modifier.height(38.dp)
    ) {
        val label = when {
            isReady -> "$title: READY!"
            else -> "$title ($cost¢)"
        }
        Text(
            text = label,
            color = if (isReady) Color.White else Color(0xFF235C75),
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}
