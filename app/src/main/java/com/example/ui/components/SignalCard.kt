package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SignalDirection
import com.example.data.model.TradingSignal
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BearishRedGlow
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.BullishGreenGlow
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldBorderLight
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimaryGold
import com.example.ui.theme.WarningAmber
import java.util.Locale

@Composable
fun SignalCard(
    signal: TradingSignal?,
    isScanning: Boolean,
    scanProgress: Float,
    countdownSeconds: Long,
    maxCountdownSeconds: Long,
    isCountdownActive: Boolean,
    onTriggerScan: () -> Unit,
    onViewConfirmations: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar_spin")
    val radarRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_angle"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.5.dp,
                Brush.verticalGradient(listOf(GoldSecondary, GoldBorder)),
                RoundedCornerShape(16.dp)
            )
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF191712),
                        Color(0xFF0F0E14),
                        Color(0xFF0A0A0D)
                    )
                )
            )
            .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = GoldDark)
            .padding(14.dp)
            .testTag("signal_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Title & Scanning State
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "AI Quant Signal",
                        tint = GoldSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AI QUANT CONFLUENCE ENGINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldAccent,
                        letterSpacing = 0.5.sp
                    )
                }

                // Deep Scan Action Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isScanning) Color(0xFF262114) else GoldPrimary)
                        .border(1.dp, GoldSecondary, RoundedCornerShape(20.dp))
                        .clickable(enabled = !isScanning) { onTriggerScan() }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("deep_scan_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isScanning) {
                            Icon(
                                imageVector = Icons.Default.Radar,
                                contentDescription = "Scanning",
                                tint = GoldSecondary,
                                modifier = Modifier
                                    .size(13.dp)
                                    .rotate(radarRotation)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "SCANNING...",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldSecondary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Scan",
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "DEEP SCAN",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            // Scanning Animated Progress Bar
            if (isScanning) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Scanning 35 Technical Indicators & Structural Flow...",
                            fontSize = 8.5.sp,
                            color = GoldAccent
                        )
                        Text(
                            text = "${(scanProgress * 100).toInt()}%",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { scanProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = GoldSecondary,
                        trackColor = Color(0xFF222018)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Signal Output Box
            val dir = signal?.direction ?: SignalDirection.WAIT_CONFIRMATION
            val isCall = dir == SignalDirection.CALL_UP
            val isPut = dir == SignalDirection.PUT_DOWN

            val signalBg = when {
                isCall -> Brush.horizontalGradient(listOf(Color(0xFF0F2618), Color(0xFF131A16)))
                isPut -> Brush.horizontalGradient(listOf(Color(0xFF2B120E), Color(0xFF181112)))
                else -> Brush.horizontalGradient(listOf(Color(0xFF1C1A14), Color(0xFF121216)))
            }

            val signalBorder = when {
                isCall -> BullishGreen
                isPut -> BearishRed
                else -> GoldBorderLight
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(signalBg)
                    .border(1.5.dp, signalBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: Direction & Confidence
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCall -> BullishGreen.copy(alpha = 0.2f)
                                        isPut -> BearishRed.copy(alpha = 0.2f)
                                        else -> GoldPrimary.copy(alpha = 0.15f)
                                    }
                                )
                                .border(
                                    1.dp,
                                    when {
                                        isCall -> BullishGreen
                                        isPut -> BearishRed
                                        else -> GoldPrimary
                                    },
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when {
                                    isCall -> Icons.Default.ArrowUpward
                                    isPut -> Icons.Default.ArrowDownward
                                    else -> Icons.Default.HourglassBottom
                                },
                                contentDescription = dir.label,
                                tint = when {
                                    isCall -> BullishGreen
                                    isPut -> BearishRed
                                    else -> GoldSecondary
                                },
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = dir.label,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = when {
                                    isCall -> BullishGreen
                                    isPut -> BearishRed
                                    else -> WarningAmber
                                }
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Confidence: ",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = String.format(Locale.US, "%.1f%%", signal?.confidencePercentage ?: 50.0),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )
                            }
                        }
                    }

                    // Right: Live Countdown Expiry Timer
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "EXPIRY COUNTDOWN",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF000000))
                                .border(1.dp, if (isCountdownActive) GoldSecondary else GoldBorder, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = formatCountdown(if (isCountdownActive) countdownSeconds else maxCountdownSeconds),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = if (isCountdownActive) GoldSecondary else TextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details and Confluence Rationale
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Candle Pattern Tag
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceCard)
                        .border(0.5.dp, GoldBorder, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text("CANDLE PATTERN", fontSize = 7.5.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = signal?.candlePattern ?: "Analyzing price action",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimaryGold,
                            maxLines = 1
                        )
                    }
                }

                // Market Structure Tag
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceCard)
                        .border(0.5.dp, GoldBorder, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text("MARKET STRUCTURE", fontSize = 7.5.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = signal?.marketStructure ?: "Order flow equilibrium",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimaryGold,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // AI Context Explanation
            signal?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F0E13))
                        .border(0.5.dp, Color(0xFF332D1D), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = it.aiRationale,
                        fontSize = 9.sp,
                        color = Color(0xFFD4CBB6),
                        lineHeight = 12.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Confluence Breakdown Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onViewConfirmations() }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "View Confirmation Matrix",
                    tint = GoldSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "View 35-Factor Confluence Breakdown Matrix",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldSecondary
                )
            }
        }
    }
}

private fun formatCountdown(seconds: Long): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format(Locale.US, "%02d:%02d", mins, secs)
}
