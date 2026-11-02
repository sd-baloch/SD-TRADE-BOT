package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimaryGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsSheet(
    onDismiss: () -> Unit
) {
    var minConfluenceThreshold by remember { mutableFloatStateOf(65f) }
    var antiFakeStrictEnforcement by remember { mutableStateOf(true) }
    var geminiAiContextEnabled by remember { mutableStateOf(true) }
    var highFrequencyTickStream by remember { mutableStateOf(true) }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.5.dp, GoldSecondary, RoundedCornerShape(16.dp))
            .testTag("admin_settings_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Admin",
                        tint = GoldSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ADMIN & ALGO SETTINGS",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldAccent
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = GoldPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section 1: 30/50 Confluence Sensitivity
                item {
                    SettingCard {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("30/50 Confluence Threshold", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = TextPrimaryGold)
                                Text("${minConfluenceThreshold.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Black, color = GoldSecondary)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Minimum weighted alignment required from 35 mathematical checks before generating a signal.",
                                fontSize = 8.5.sp,
                                color = TextMuted
                            )
                            Slider(
                                value = minConfluenceThreshold,
                                onValueChange = { minConfluenceThreshold = it },
                                valueRange = 55f..80f,
                                steps = 5,
                                colors = SliderDefaults.colors(
                                    thumbColor = GoldSecondary,
                                    activeTrackColor = GoldPrimary,
                                    inactiveTrackColor = Color(0xFF262014)
                                )
                            )
                        }
                    }
                }

                // Section 2: Anti-Fake Enforcement
                item {
                    SettingCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Anti-Fake Guard Active", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = TextPrimaryGold)
                                Text(
                                    "Strictly suppresses unverified signals when data is delayed or indicators conflict.",
                                    fontSize = 8.5.sp,
                                    color = TextMuted
                                )
                            }
                            Switch(
                                checked = antiFakeStrictEnforcement,
                                onCheckedChange = { antiFakeStrictEnforcement = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = GoldSecondary,
                                    checkedTrackColor = Color(0xFF332912)
                                )
                            )
                        }
                    }
                }

                // Section 3: Gemini AI Assistant Mode
                item {
                    SettingCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Gemini AI Analytical Copilot", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = TextPrimaryGold)
                                Text(
                                    "Interprets calculated quantitative variables into institutional structural insights.",
                                    fontSize = 8.5.sp,
                                    color = TextMuted
                                )
                            }
                            Switch(
                                checked = geminiAiContextEnabled,
                                onCheckedChange = { geminiAiContextEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = GoldSecondary,
                                    checkedTrackColor = Color(0xFF332912)
                                )
                            )
                        }
                    }
                }

                // Section 4: Data Engine Status
                item {
                    SettingCard {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = "Verified", tint = BullishGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Provider Infrastructure", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = TextPrimaryGold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("• Binance Spot Real-Time WebSocket API (Crypto)", fontSize = 8.5.sp, color = TextMuted)
                            Text("• Interbank Forex Institutional Feed (FX Majors/Minors)", fontSize = 8.5.sp, color = TextMuted)
                            Text("• London Bullion Market Association (Gold/Silver)", fontSize = 8.5.sp, color = TextMuted)
                            Text("• WhatsApp Support Desk: +923316813901", fontSize = 8.5.sp, color = GoldAccent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF13131A))
            .border(0.8.dp, GoldBorder, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        content()
    }
}
