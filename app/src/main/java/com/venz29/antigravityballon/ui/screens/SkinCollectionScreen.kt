package com.venz29.antigravityballon.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venz29.antigravityballon.core.models.BalloonSkin
import com.venz29.antigravityballon.core.models.Rarity
import com.venz29.antigravityballon.core.models.SkinCatalog
import com.venz29.antigravityballon.ui.canvas.GameCanvasRenderer
import kotlin.math.sin

@Composable
fun SkinCollectionScreen(
    wallet: Int,
    unlockedSkinIds: Set<String>,
    equippedSkinId: String,
    time: Float,
    onSkinEquipped: (String) -> Unit,
    onBackClicked: () -> Unit
) {
    var previewSkinId by remember { mutableStateOf(equippedSkinId) }
    val previewSkin = SkinCatalog.findById(previewSkinId)
    val isPreviewOwned = previewSkin.id in unlockedSkinIds
    val isPreviewEquipped = previewSkin.id == equippedSkinId

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
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
                    contentDescription = "Back to Menu",
                    tint = Color(0xFF1B4E63)
                )
            }

            Text(
                text = "WARDROBE",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1B4E63),
                letterSpacing = 2.sp
            )

            CoinBadge(wallet)
        }

        Spacer(Modifier.height(14.dp))

        // Balloon Preview Stage Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(26.dp))
                .background(Color.White.copy(alpha = 0.94f), RoundedCornerShape(26.dp))
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val bob = sin(time * 2.5f).toFloat() * 6f
                    val tilt = sin(time * 1.8f).toFloat() * 7f
                    GameCanvasRenderer.drawBalloon(
                        drawScope = this,
                        x = size.width / 2f,
                        y = size.height / 2f + bob,
                        scale = 1.35f,
                        tilt = tilt,
                        skin = previewSkin,
                        isBlinking = false,
                        hasShield = false,
                        time = time
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = previewSkin.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1B4E63)
                )
                Spacer(Modifier.width(8.dp))
                RarityBadge(previewSkin.rarity)
            }

            Text(
                text = previewSkin.description,
                fontSize = 12.sp,
                color = Color(0xFF5A8B9E),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            // Equip or Lock Button
            when {
                isPreviewEquipped -> {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(16.dp))
                            .padding(horizontal = 24.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "✓ CURRENTLY EQUIPPED",
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
                isPreviewOwned -> {
                    Button(
                        onClick = { onSkinEquipped(previewSkin.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                        modifier = Modifier
                            .fillMaxWidth(0.65f)
                            .height(44.dp)
                    ) {
                        Text(
                            text = "EQUIP SKIN",
                            color = Color(0xFF664600),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF757575), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "LOCKED · UNLOCK IN GACHA",
                                color = Color(0xFF757575),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Skin Collection List
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ALL BALLOONS (${unlockedSkinIds.size}/${SkinCatalog.ALL_SKINS.size})",
                color = Color(0xFF1B4E63),
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(SkinCatalog.ALL_SKINS) { skin ->
                val isOwned = skin.id in unlockedSkinIds
                val isEquipped = skin.id == equippedSkinId
                val isSelected = skin.id == previewSkinId

                SkinItemRow(
                    skin = skin,
                    isOwned = isOwned,
                    isEquipped = isEquipped,
                    isSelected = isSelected,
                    onClick = { previewSkinId = skin.id }
                )
            }
        }
    }
}

@Composable
private fun SkinItemRow(
    skin: BalloonSkin,
    isOwned: Boolean,
    isEquipped: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFFFFC107) else Color.Transparent
    val backgroundColor = if (isOwned) Color.White.copy(alpha = 0.88f) else Color.White.copy(alpha = 0.55f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Swatch
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(skin.primaryColorHex), Color(skin.secondaryColorHex))
                    ),
                    CircleShape
                )
        )

        Spacer(Modifier.width(12.dp))

        // Details
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = skin.name,
                    color = if (isOwned) Color(0xFF1B4E63) else Color(0xFF78909C),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(Modifier.width(6.dp))
                RarityBadge(skin.rarity)
            }
            Text(
                text = if (isOwned) skin.pattern.name.lowercase().replace("_", " ") else "Locked",
                color = Color(0xFF90A4AE),
                fontSize = 11.sp
            )
        }

        // Status Badge
        when {
            isEquipped -> {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE8F5E9), CircleShape)
                        .padding(6.dp)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Equipped",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            !isOwned -> {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color(0xFFB0BEC5),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun RarityBadge(rarity: Rarity) {
    Box(
        modifier = Modifier
            .background(Color(rarity.badgeColorHex).copy(alpha = 0.16f), RoundedCornerShape(8.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        Text(
            text = rarity.displayName.uppercase(),
            color = Color(rarity.badgeColorHex),
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp
        )
    }
}
