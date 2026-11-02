package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Candle
import com.example.data.model.Instrument
import com.example.engine.MarketStructureAnalysis
import com.example.engine.TechnicalAnalysisResult
import com.example.ui.ChartStyle
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.NeutralBlue
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimaryGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

@Composable
fun CandlestickChart(
    instrument: Instrument,
    candles: List<Candle>,
    currentPrice: Double,
    indicators: TechnicalAnalysisResult?,
    marketStructure: MarketStructureAnalysis?,
    chartStyle: ChartStyle,
    onToggleChartStyle: () -> Unit,
    onOpenPairSelector: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCandleIndex by remember { mutableStateOf<Int?>(null) }
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF09090D))
            .border(1.dp, GoldBorder, RoundedCornerShape(12.dp))
            .testTag("candlestick_chart_container")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Chart Header Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Pair Name & Live Price
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures { onOpenPairSelector() }
                    }
                ) {
                    Text(
                        text = instrument.displayName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formatPrice(currentPrice, instrument.decimals),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if ((candles.lastOrNull()?.isBullish) != false) BullishGreen else BearishRed
                    )
                }

                // Overlay Status Tags
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF1B1B26))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (chartStyle == ChartStyle.NATIVE_CANDLE) "NATIVE CANDLE" else "TRADINGVIEW",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldSecondary
                        )
                    }

                    // S&R Level hint
                    if (marketStructure != null) {
                        Text(
                            text = "S:${formatPrice(marketStructure.supportLevel, 2)} R:${formatPrice(marketStructure.resistanceLevel, 2)}",
                            fontSize = 8.5.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            // Main Canvas Area
            if (candles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Synchronizing live tick candles...",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(candles) {
                                detectTapGestures(
                                    onPress = { offset ->
                                        val visibleCount = min(candles.size, 45)
                                        val candleWidth = size.width / visibleCount
                                        val idx = (offset.x / candleWidth).toInt().coerceIn(0, visibleCount - 1)
                                        val startIdx = max(0, candles.size - visibleCount)
                                        selectedCandleIndex = startIdx + idx
                                    }
                                )
                            }
                    ) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        val chartHeight = canvasHeight * 0.78f
                        val volumeHeight = canvasHeight * 0.22f

                        val visibleCount = min(candles.size, 45)
                        val displayCandles = candles.takeLast(visibleCount)
                        if (displayCandles.isEmpty()) return@Canvas

                        var minPrice = displayCandles.minOf { it.low }
                        var maxPrice = displayCandles.maxOf { it.high }
                        if (minPrice == maxPrice) {
                            minPrice -= 0.001
                            maxPrice += 0.001
                        }
                        val priceRange = maxPrice - minPrice
                        val maxVolume = displayCandles.maxOfOrNull { it.volume } ?: 1.0

                        val candleStep = canvasWidth / visibleCount
                        val candleBarWidth = (candleStep * 0.65f).coerceAtLeast(2f)

                        // 1. Draw Support and Resistance lines
                        if (marketStructure != null) {
                            val supY = chartHeight - ((marketStructure.supportLevel - minPrice) / priceRange * chartHeight).toFloat()
                            val resY = chartHeight - ((marketStructure.resistanceLevel - minPrice) / priceRange * chartHeight).toFloat()

                            if (supY in 0f..chartHeight) {
                                drawLine(
                                    color = BullishGreen.copy(alpha = 0.45f),
                                    start = Offset(0f, supY),
                                    end = Offset(canvasWidth, supY),
                                    strokeWidth = 1.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                                )
                            }
                            if (resY in 0f..chartHeight) {
                                drawLine(
                                    color = BearishRed.copy(alpha = 0.45f),
                                    start = Offset(0f, resY),
                                    end = Offset(canvasWidth, resY),
                                    strokeWidth = 1.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                                )
                            }
                        }

                        // 2. Draw Candlesticks & Volume
                        displayCandles.forEachIndexed { i, c ->
                            val xCenter = (i * candleStep) + (candleStep / 2f)
                            val isBullish = c.isBullish
                            val candleColor = if (isBullish) BullishGreen else BearishRed

                            val yOpen = chartHeight - ((c.open - minPrice) / priceRange * chartHeight).toFloat()
                            val yClose = chartHeight - ((c.close - minPrice) / priceRange * chartHeight).toFloat()
                            val yHigh = chartHeight - ((c.high - minPrice) / priceRange * chartHeight).toFloat()
                            val yLow = chartHeight - ((c.low - minPrice) / priceRange * chartHeight).toFloat()

                            // Wick line
                            drawLine(
                                color = candleColor,
                                start = Offset(xCenter, yHigh),
                                end = Offset(xCenter, yLow),
                                strokeWidth = 1.5.dp.toPx()
                            )

                            // Candle Body
                            val bodyTop = min(yOpen, yClose)
                            val bodyBottom = max(yOpen, yClose)
                            val bodyHeight = max(1.5f, bodyBottom - bodyTop)

                            drawRect(
                                color = candleColor,
                                topLeft = Offset(xCenter - (candleBarWidth / 2f), bodyTop),
                                size = Size(candleBarWidth, bodyHeight)
                            )

                            // Volume Bar at bottom
                            val vRatio = (c.volume / maxVolume).toFloat().coerceIn(0f, 1f)
                            val vBarHeight = vRatio * (volumeHeight - 4f)
                            val vTop = canvasHeight - vBarHeight

                            drawRect(
                                color = candleColor.copy(alpha = 0.35f),
                                topLeft = Offset(xCenter - (candleBarWidth / 2f), vTop),
                                size = Size(candleBarWidth, vBarHeight)
                            )
                        }

                        // 3. Live Price Dotted Line
                        val liveY = chartHeight - ((currentPrice - minPrice) / priceRange * chartHeight).toFloat()
                        if (liveY in 0f..canvasHeight) {
                            drawLine(
                                color = GoldSecondary,
                                start = Offset(0f, liveY),
                                end = Offset(canvasWidth, liveY),
                                strokeWidth = 1.2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
                            )
                            drawCircle(
                                color = GoldSecondary,
                                radius = 3.dp.toPx(),
                                center = Offset(canvasWidth - 8.dp.toPx(), liveY)
                            )
                        }
                    }

                    // Touch inspection badge
                    selectedCandleIndex?.let { idx ->
                        if (idx in candles.indices) {
                            val sel = candles[idx]
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .align(Alignment.TopStart)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xDD000000))
                                    .border(1.dp, GoldBorder, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "O:${formatPrice(sel.open, instrument.decimals)} H:${formatPrice(sel.high, instrument.decimals)} L:${formatPrice(sel.low, instrument.decimals)} C:${formatPrice(sel.close, instrument.decimals)}",
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = GoldAccent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatPrice(price: Double, decimals: Int): String {
    return String.format(Locale.US, "%.${decimals}f", price)
}
