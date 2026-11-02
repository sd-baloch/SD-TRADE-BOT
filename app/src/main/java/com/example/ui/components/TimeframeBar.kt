package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TimeframeOption
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimaryGold

@Composable
fun TimeframeBar(
    selectedTimeframe: TimeframeOption,
    customSeconds: Int,
    onTimeframeSelected: (TimeframeOption) -> Unit,
    onCustomSecondsChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCustomDialog by remember { mutableStateOf(false) }
    var tempSecondsText by remember { mutableStateOf(customSeconds.toString()) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimeframeOption.entries.forEach { tf ->
            val isSelected = tf == selectedTimeframe
            val label = if (tf == TimeframeOption.CUSTOM) {
                "${customSeconds}S (CST)"
            } else {
                tf.label
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) GoldPrimary else SurfaceCard)
                    .border(
                        1.dp,
                        if (isSelected) GoldSecondary else GoldBorder,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        if (tf == TimeframeOption.CUSTOM) {
                            showCustomDialog = true
                        }
                        onTimeframeSelected(tf)
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("timeframe_${tf.name.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 10.5.sp,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                    color = if (isSelected) Color.Black else TextPrimaryGold
                )
            }
        }
    }

    if (showCustomDialog) {
        AlertDialog(
            onDismissRequest = { showCustomDialog = false },
            containerColor = SurfaceDark,
            title = {
                Text(
                    text = "Custom Expiry Timeframe",
                    color = GoldAccent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = tempSecondsText,
                    onValueChange = { tempSecondsText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Duration in seconds (e.g. 15, 45, 120)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val parsed = tempSecondsText.toIntOrNull()?.coerceIn(5, 3600) ?: 45
                        onCustomSecondsChanged(parsed)
                        onTimeframeSelected(TimeframeOption.CUSTOM)
                        showCustomDialog = false
                    }
                ) {
                    Text("Apply Timeframe", color = GoldSecondary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }
}
