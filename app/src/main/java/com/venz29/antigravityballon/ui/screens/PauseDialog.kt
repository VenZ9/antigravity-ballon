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
fun PauseDialog(
    onResumeClicked: () -> Unit,
    onRestartClicked: () -> Unit,
    onMainMenuClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .widthIn(max = 380.dp)
            .shadow(20.dp, RoundedCornerShape(32.dp))
            .background(Color.White.copy(alpha = 0.96f), RoundedCornerShape(32.dp))
            .padding(horizontal = 26.dp, vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "GAME PAUSED",
            color = Color(0xFF1B4E63),
            fontWeight = FontWeight.Black,
            fontSize = 25.sp,
            letterSpacing = 1.2.sp
        )

        Text(
            text = "Your balloon is floating safely in place.",
            color = Color(0xFF5A8B9E),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
        )

        // Resume Button
        Button(
            onClick = onResumeClicked,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "RESUME FLIGHT",
                color = Color(0xFF664600),
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.height(10.dp))

        // Restart Button
        OutlinedButton(
            onClick = onRestartClicked,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1B4E63)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "RESTART",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp
            )
        }

        Spacer(Modifier.height(10.dp))

        // Main Menu Button
        Button(
            onClick = onMainMenuClicked,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "MAIN MENU",
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
            )
        }
    }
}
