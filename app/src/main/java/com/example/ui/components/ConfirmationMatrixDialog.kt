package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CheckStatus
import com.example.data.model.ConfirmationCheck
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationMatrixDialog(
    confirmations: List<ConfirmationCheck>,
    bullishScore: Double,
    bearishScore: Double,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.5.dp, GoldSecondary, RoundedCornerShape(16.dp))
            .testTag("confirmation_matrix_dialog")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title & Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "35-FACTOR CONFLUENCE MATRIX",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldAccent
                    )
                    Text(
                        text = "Mathematical Verification & Anti-Fake Audit",
                        fontSize = 9.sp,
                        color = TextMuted
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = GoldPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Score Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
                        Text("BULLISH SCORE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text("${"%.1f".format(bullishScore)}%", fontSize = 14.sp, fontWeight = FontWeight.Black, color = BullishGreen)
                    }
                }

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
                        Text("BEARISH SCORE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text("${"%.1f".format(bearishScore)}%", fontSize = 14.sp, fontWeight = FontWeight.Black, color = BearishRed)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Detailed List of Checks
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(confirmations) { check ->
                    ConfirmationRow(check)
                }
            }
        }
    }
}

@Composable
private fun ConfirmationRow(check: ConfirmationCheck) {
    val statusColor = when (check.status) {
        CheckStatus.BULLISH -> BullishGreen
        CheckStatus.BEARISH -> BearishRed
        CheckStatus.NEUTRAL -> WarningAmber
        CheckStatus.UNAVAILABLE -> TextMuted
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF14141C))
            .border(0.5.dp, GoldBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${check.id}. ${check.name}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryGold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "[${check.category}]",
                        fontSize = 8.sp,
                        color = TextMuted
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${check.valueStr} • ${check.detail}",
                    fontSize = 8.5.sp,
                    color = Color(0xFFC0BCB0),
                    maxLines = 1
                )
            }

            // Status Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(statusColor.copy(alpha = 0.15f))
                    .border(1.dp, statusColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = check.status.label,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Black,
                    color = statusColor
                )
            }
        }
    }
}
