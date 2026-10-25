package com.venz29.antigravityballon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venz29.antigravityballon.core.engine.GameEngine

@Composable
fun GamePlayHUD(
    engine: GameEngine,
    onPauseClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // Altitude Score Card & Active Boost Badges
        Column(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.82f), RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "ALTITUDE",
                color = Color(0xFF4C8EA8),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 1.2.sp
            )
            Text(
                text = "${engine.score}m",
                color = Color(0xFF1B4E63),
                fontWeight = FontWeight.Black,
                fontSize = 24.sp
            )

            if (engine.hasShield) {
                Text(
                    text = "SHIELD ACTIVE",
                    color = Color(0xFF0091EA),
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp
                )
            } else if (engine.isDoubleCoins) {
                Text(
                    text = "2× COINS (${engine.doubleCoinsTimer.toInt()}s)",
                    color = Color(0xFFFF8F00),
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp
                )
            }
        }

        // Lives & Coin Badge & Pause Button
        Row(verticalAlignment = Alignment.CenterVertically) {
            CoinBadge(engine.runCoins)

            Spacer(Modifier.width(10.dp))

            // 3 Hearts Life indicator
            Row(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.82f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..3) {
                    val isFull = i <= engine.lives
                    Text(
                        text = if (isFull) "♥" else "♡",
                        fontSize = 18.sp,
                        color = if (isFull) Color(0xFFFF3366) else Color(0xFFFF99AA),
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 1.dp)
                    )
                }
            }

            Spacer(Modifier.width(10.dp))

            IconButton(
                onClick = onPauseClicked,
                modifier = Modifier.background(Color.White.copy(alpha = 0.85f), CircleShape)
            ) {
                Icon(
                    Icons.Default.Pause,
                    contentDescription = "Pause",
                    tint = Color(0xFF235C75)
                )
            }
        }
    }
}
