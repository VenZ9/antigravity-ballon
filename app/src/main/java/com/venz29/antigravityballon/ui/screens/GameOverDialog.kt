package com.venz29.antigravityballon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameOverDialog(
    score: Int,
    bestScore: Int,
    runCoins: Int,
    totalCoins: Int,
    isNewRecord: Boolean,
    onRestartClicked: () -> Unit,
    onMainMenuClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(0.88f)
            .widthIn(max = 380.dp)
            .shadow(24.dp, RoundedCornerShape(32.dp))
            .background(Color.White.copy(alpha = 0.96f), RoundedCornerShape(32.dp))
            .padding(horizontal = 26.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isNewRecord) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFFF9800), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "★ NEW RECORD! ★",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        Text(
            text = "FLIGHT OVER",
            color = Color(0xFF1B4E63),
            fontWeight = FontWeight.Black,
            fontSize = 28.sp,
            letterSpacing = 1.sp
        )

        Spacer(Modifier.height(14.dp))

        // Stats Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF1F9FC), RoundedCornerShape(20.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Final Altitude", color = Color(0xFF5A8B9E), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text("${score}m", color = Color(0xFF1B4E63), fontSize = 20.sp, fontWeight = FontWeight.Black)
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Best Flight", color = Color(0xFF5A8B9E), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text("${bestScore}m", color = Color(0xFF00838F), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Flight Reward", color = Color(0xFF5A8B9E), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text("+$runCoins ¢", color = Color(0xFFE65100), fontSize = 18.sp, fontWeight = FontWeight.Black)
            }

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Balance", color = Color(0xFF78909C), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Text("$totalCoins ¢", color = Color(0xFF5D4037), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(20.dp))

        // Restart ("FLY AGAIN") Button
        Button(
            onClick = onRestartClicked,
            shape = RoundedCornerShape(22.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(
                text = "FLY AGAIN",
                color = Color(0xFF664600),
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.height(10.dp))

        // Main Menu Button
        OutlinedButton(
            onClick = onMainMenuClicked,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1B4E63)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "MAIN MENU",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }
    }
}
