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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PerformanceStats
import com.example.data.model.SignalDirection
import com.example.data.model.SignalHistoryRecord
import com.example.data.model.SignalRecordStatus
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimaryGold
import com.example.ui.theme.WarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceDashboard(
    stats: PerformanceStats,
    history: List<SignalHistoryRecord>,
    onClearHistory: () -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.5.dp, GoldSecondary, RoundedCornerShape(16.dp))
            .testTag("performance_dashboard_dialog")
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
                Column {
                    Text(
                        text = "LIVE ACCURACY & AUDIT",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldAccent
                    )
                    Text(
                        text = "Unmanipulated Real Execution Metrics",
                        fontSize = 8.5.sp,
                        color = TextMuted
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onClearHistory, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear History",
                            tint = TextMuted
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
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stat Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Win Rate Card
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF211C11))
                        .border(1.dp, GoldSecondary, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("WIN RATE", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text(
                            text = String.format(Locale.US, "%.1f%%", stats.winRate),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldSecondary
                        )
                    }
                }

                // Won
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F2618))
                        .border(1.dp, BullishGreen, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("WON", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text("${stats.won}", fontSize = 17.sp, fontWeight = FontWeight.Black, color = BullishGreen)
                    }
                }

                // Lost
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF26100E))
                        .border(1.dp, BearishRed, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("LOST", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text("${stats.lost}", fontSize = 17.sp, fontWeight = FontWeight.Black, color = BearishRed)
                    }
                }

                // Total
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF14141E))
                        .border(1.dp, GoldBorder, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("TOTAL", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text("${stats.totalSignals}", fontSize = 17.sp, fontWeight = FontWeight.Black, color = TextPrimaryGold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "SIGNAL EXECUTION LOG",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (history.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No trading signals generated yet.\nTap 'DEEP SCAN' to initiate mathematical analysis.",
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(history) { record ->
                        HistoryRecordRow(record, dateFormat)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryRecordRow(
    record: SignalHistoryRecord,
    dateFormat: SimpleDateFormat
) {
    val isCall = record.direction == SignalDirection.CALL_UP
    val statusColor = when (record.status) {
        SignalRecordStatus.WON -> BullishGreen
        SignalRecordStatus.LOST -> BearishRed
        SignalRecordStatus.ACTIVE -> GoldSecondary
        SignalRecordStatus.EXPIRED -> TextMuted
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF13131B))
            .border(0.5.dp, GoldBorder, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = record.symbol,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryGold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = record.direction.label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isCall) BullishGreen else BearishRed
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${record.expirySeconds}s expiry)",
                        fontSize = 8.5.sp,
                        color = TextMuted
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Entry: ${record.entryPrice} | Close: ${record.closingPrice ?: "Running..."} • ${dateFormat.format(Date(record.timestamp))}",
                    fontSize = 8.5.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFB3ADA0)
                )
            }

            // Status Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(statusColor.copy(alpha = 0.15f))
                    .border(1.dp, statusColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = record.status.name,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = statusColor
                )
            }
        }
    }
}
