package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ConnectionStatus
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.NeutralBlue
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.WarningAmber

@Composable
fun HeaderBranding(
    connectionStatus: ConnectionStatus,
    latencyMs: Long,
    onOpenStats: () -> Unit,
    onOpenAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Continuous smooth blink transition for the 3 Red, Green, Blue dots in sequence
    val transition = rememberInfiniteTransition(label = "dots_blink")
    
    val dotPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dot_phase"
    )

    // Compute opacity for Red, Green, Blue dots in sequence
    val redAlpha = when {
        dotPhase in 0f..1f -> 1f
        dotPhase in 1f..1.5f -> 1f - (dotPhase - 1f) * 1.5f
        else -> 0.35f
    }.coerceIn(0.35f, 1f)

    val greenAlpha = when {
        dotPhase in 1f..2f -> 1f
        dotPhase in 2f..2.5f -> 1f - (dotPhase - 2f) * 1.5f
        else -> 0.35f
    }.coerceIn(0.35f, 1f)

    val blueAlpha = when {
        dotPhase in 2f..3f -> 1f
        dotPhase in 0f..0.5f -> 1f - (dotPhase) * 1.5f
        else -> 0.35f
    }.coerceIn(0.35f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF14130F),
                        Color(0xFF0A0A0E)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        Color(0x33D4AF37),
                        Color(0xFF8A7334),
                        Color(0x33D4AF37)
                    )
                ),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // 3D Metallic Emblem Logo
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, GoldPrimary, CircleShape)
                        .shadow(8.dp, CircleShape, spotColor = GoldSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_fg_1793642120964),
                        contentDescription = "SD BALOCH 3D Emblem",
                        modifier = Modifier.size(44.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "SD BALOCH",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.SansSerif,
                            color = GoldAccent,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "QX BOT",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // 3 Smoothly Blinking Dots: Red, Green, Blue in sequence
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.testTag("blinking_indicator_dots")
                        ) {
                            // 1. Red Dot
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(BearishRed.copy(alpha = redAlpha))
                                    .border(0.5.dp, BearishRed, CircleShape)
                            )
                            // 2. Green Dot
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(BullishGreen.copy(alpha = greenAlpha))
                                    .border(0.5.dp, BullishGreen, CircleShape)
                            )
                            // 3. Blue Dot
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(NeutralBlue.copy(alpha = blueAlpha))
                                    .border(0.5.dp, NeutralBlue, CircleShape)
                            )
                        }
                    }

                    // Live Status and Latency
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        val statusColor = when (connectionStatus) {
                            ConnectionStatus.CONNECTED_LIVE -> BullishGreen
                            ConnectionStatus.DATA_DELAYED -> WarningAmber
                            ConnectionStatus.DISCONNECTED, ConnectionStatus.UNAVAILABLE -> BearishRed
                        }

                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = connectionStatus.label,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = statusColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${latencyMs}ms",
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            // Action Icons (Accuracy Stats & Settings)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onOpenStats,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("open_stats_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = "Accuracy Stats",
                        tint = GoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onOpenAdmin,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("open_admin_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
