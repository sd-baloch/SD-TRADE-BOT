package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlatformMode
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldBorderLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimaryGold

@Composable
fun PlatformSelector(
    activePlatform: PlatformMode,
    onPlatformSelected: (PlatformMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Card 1: Quotex Analysis
        PlatformCard(
            platform = PlatformMode.QUOTEX,
            isSelected = activePlatform == PlatformMode.QUOTEX,
            icon = Icons.Default.Speed,
            badge = "OTC / BINARY",
            tag = "platform_quotex_card",
            modifier = Modifier.weight(1f),
            onClick = { onPlatformSelected(PlatformMode.QUOTEX) }
        )

        // Card 2: TradingView Analysis
        PlatformCard(
            platform = PlatformMode.TRADINGVIEW,
            isSelected = activePlatform == PlatformMode.TRADINGVIEW,
            icon = Icons.Default.CandlestickChart,
            badge = "INSTITUTIONAL SPOT",
            tag = "platform_tradingview_card",
            modifier = Modifier.weight(1f),
            onClick = { onPlatformSelected(PlatformMode.TRADINGVIEW) }
        )
    }
}

@Composable
private fun PlatformCard(
    platform: PlatformMode,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badge: String,
    tag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) GoldSecondary else GoldBorder
    val borderWidth = if (isSelected) 1.5.dp else 1.dp
    val bgBrush = if (isSelected) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF262114),
                Color(0xFF14141B)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xFF13131A),
                Color(0xFF0C0C12)
            )
        )
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            .background(bgBrush)
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) GoldPrimary.copy(alpha = 0.2f) else Color(0xFF1C1C24)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = platform.title,
                        tint = if (isSelected) GoldSecondary else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Active Platform",
                        tint = GoldSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF1C1C26))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = platform.title,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) GoldAccent else TextPrimaryGold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = platform.shortDesc,
                fontSize = 8.5.sp,
                color = TextMuted,
                lineHeight = 11.sp,
                maxLines = 2
            )
        }
    }
}
