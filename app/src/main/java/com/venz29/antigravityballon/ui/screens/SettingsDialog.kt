package com.venz29.antigravityballon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsDialog(
    musicEnabled: Boolean,
    sfxEnabled: Boolean,
    highSensitivity: Boolean,
    onMusicToggled: (Boolean) -> Unit,
    onSfxToggled: (Boolean) -> Unit,
    onSensitivityToggled: (Boolean) -> Unit,
    onResetProgress: () -> Unit,
    onDismiss: () -> Unit
) {
    var showConfirmReset by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                text = "SETTINGS & GUIDE",
                fontWeight = FontWeight.Black,
                color = Color(0xFF1B4E63),
                fontSize = 19.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "HOW TO PLAY",
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF4C8EA8),
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "• Drag anywhere on the screen to freely steer the balloon.\n• Dodge storm clouds, birds, crystal spikes, stars, and drones.\n• Collect gold coins along your flight path to spin for new skins.\n• Collisions consume 1 heart with brief invulnerability.",
                    color = Color(0xFF5A8B9E),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                )

                Divider(color = Color(0xFFE0E0E0))

                Spacer(Modifier.height(10.dp))

                // Music Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Background Music", color = Color(0xFF1B4E63), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Switch(
                        checked = musicEnabled,
                        onCheckedChange = onMusicToggled,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFC107), checkedTrackColor = Color(0xFFFFE082))
                    )
                }

                // SFX Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sound Effects", color = Color(0xFF1B4E63), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Switch(
                        checked = sfxEnabled,
                        onCheckedChange = onSfxToggled,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFC107), checkedTrackColor = Color(0xFFFFE082))
                    )
                }

                // Control Sensitivity
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("High Sensitivity", color = Color(0xFF1B4E63), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Switch(
                        checked = highSensitivity,
                        onCheckedChange = onSensitivityToggled,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFC107), checkedTrackColor = Color(0xFFFFE082))
                    )
                }

                Spacer(Modifier.height(10.dp))
                Divider(color = Color(0xFFE0E0E0))
                Spacer(Modifier.height(10.dp))

                // Reset Progress Button
                if (!showConfirmReset) {
                    TextButton(
                        onClick = { showConfirmReset = true },
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Text("Reset Saved Progress", color = Color(0xFFE53935), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFEBEE), RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Are you sure?", color = Color(0xFFC62828), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row {
                            TextButton(onClick = {
                                onResetProgress()
                                showConfirmReset = false
                            }) {
                                Text("YES, RESET", color = Color(0xFFC62828), fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                            TextButton(onClick = { showConfirmReset = false }) {
                                Text("CANCEL", color = Color(0xFF555555), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
            ) {
                Text("DONE", color = Color(0xFF664600), fontWeight = FontWeight.Black)
            }
        }
    )
}
