package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlatformMode
import com.example.ui.components.AdminSettingsSheet
import com.example.ui.components.AssetSelectorSheet
import com.example.ui.components.CandlestickChart
import com.example.ui.components.ConfirmationMatrixDialog
import com.example.ui.components.FloatingWhatsAppButton
import com.example.ui.components.HeaderBranding
import com.example.ui.components.PerformanceDashboard
import com.example.ui.components.PlatformSelector
import com.example.ui.components.SignalCard
import com.example.ui.components.TimeframeBar
import com.example.ui.components.TradingViewWebChart
import com.example.ui.theme.BackgroundBlack
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldBorderLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimaryGold
import java.util.Locale

@Composable
fun TradingDashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundBlack)
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = BackgroundBlack,
        floatingActionButton = {
            // Official Floating WhatsApp Button fixed at bottom-right corner
            FloatingWhatsAppButton(
                modifier = Modifier
                    .padding(end = 4.dp, bottom = 12.dp)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 76.dp)
            ) {
                // 1. Luxury Header Branding with 3D logo & 3 blinking dots (Red, Green, Blue)
                HeaderBranding(
                    connectionStatus = state.connectionStatus,
                    latencyMs = state.latencyMs,
                    onOpenStats = { viewModel.togglePerformanceStats(true) },
                    onOpenAdmin = { viewModel.toggleAdminSheet(true) }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 2. Dual Platform Selector (QUOTEX vs TRADINGVIEW)
                PlatformSelector(
                    activePlatform = state.platformMode,
                    onPlatformSelected = { viewModel.setPlatformMode(it) }
                )

                // 3. Asset Selector Bar & Quick Stats
                AssetHeaderBar(
                    displayName = state.selectedInstrument.displayName,
                    currentPrice = state.currentPrice,
                    decimals = state.selectedInstrument.decimals,
                    providerName = state.providerName,
                    isQuotex = state.platformMode == PlatformMode.QUOTEX,
                    onOpenPairSelector = { viewModel.togglePairSelector(true) }
                )

                // 4. Timeframe Selector (5s, 30s, 1m, 5m, 10m, 15m, Custom)
                TimeframeBar(
                    selectedTimeframe = state.selectedTimeframe,
                    customSeconds = state.customSeconds,
                    onTimeframeSelected = { viewModel.selectTimeframe(it) },
                    onCustomSecondsChanged = { viewModel.setCustomSeconds(it) }
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 5. Chart Engine Switcher Tab (Native Canvas vs TradingView Widget)
                ChartStyleSwitcher(
                    activeStyle = state.activeChartStyle,
                    onSwitch = { viewModel.setChartStyle(it) }
                )

                // 6. Interactive Live Chart (Native Canvas or TradingView)
                Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                    if (state.activeChartStyle == ChartStyle.NATIVE_CANDLE) {
                        CandlestickChart(
                            instrument = state.selectedInstrument,
                            candles = state.candles,
                            currentPrice = state.currentPrice,
                            indicators = state.indicators,
                            marketStructure = state.marketStructure,
                            chartStyle = state.activeChartStyle,
                            onToggleChartStyle = { viewModel.setChartStyle(ChartStyle.TRADINGVIEW_WIDGET) },
                            onOpenPairSelector = { viewModel.togglePairSelector(true) }
                        )
                    } else {
                        TradingViewWebChart(
                            instrument = state.selectedInstrument
                        )
                    }
                }

                // 7. Live Mathematical Indicators Ribbon
                LiveIndicatorsRibbon(state.indicators)

                Spacer(modifier = Modifier.height(6.dp))

                // 8. Primary Quantitative AI Signal Card with Countdown & Deep Scan
                SignalCard(
                    signal = state.currentSignal,
                    isScanning = state.isScanning,
                    scanProgress = state.scanProgress,
                    countdownSeconds = state.activeCountdownSeconds,
                    maxCountdownSeconds = state.maxCountdownSeconds,
                    isCountdownActive = state.isCountdownActive,
                    onTriggerScan = { viewModel.triggerDeepScan() },
                    onViewConfirmations = { viewModel.toggleConfirmationDialog(true) },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 9. Quotex OTC Market Transparency Notice (if Quotex mode)
                if (state.platformMode == PlatformMode.QUOTEX) {
                    QuotexNoticeCard()
                }
            }

            // Dialogs & Sheets
            if (state.showConfirmationDialog && state.currentSignal != null) {
                ConfirmationMatrixDialog(
                    confirmations = state.currentSignal!!.confirmations,
                    bullishScore = state.currentSignal!!.bullishScore,
                    bearishScore = state.currentSignal!!.bearishScore,
                    onDismiss = { viewModel.toggleConfirmationDialog(false) }
                )
            }

            if (state.showPairSelectorSheet) {
                AssetSelectorSheet(
                    instruments = viewModel.getAllInstruments(),
                    selectedInstrument = state.selectedInstrument,
                    onSelectInstrument = { viewModel.selectInstrument(it) },
                    onDismiss = { viewModel.togglePairSelector(false) }
                )
            }

            if (state.showAdminSheet) {
                AdminSettingsSheet(
                    onDismiss = { viewModel.toggleAdminSheet(false) }
                )
            }

            if (state.showPerformanceStats) {
                PerformanceDashboard(
                    stats = state.stats,
                    history = state.signalHistory,
                    onClearHistory = { viewModel.clearSignalHistory() },
                    onDismiss = { viewModel.togglePerformanceStats(false) }
                )
            }
        }
    }
}

@Composable
private fun AssetHeaderBar(
    displayName: String,
    currentPrice: Double,
    decimals: Int,
    providerName: String,
    isQuotex: Boolean,
    onOpenPairSelector: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF12121A))
            .border(1.dp, GoldBorder, RoundedCornerShape(10.dp))
            .clickable { onOpenPairSelector() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("asset_header_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = displayName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldAccent
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "Switch Pair",
                    tint = GoldSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format(Locale.US, "%.${decimals}f", currentPrice),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = GoldSecondary
                )
                Text(
                    text = if (isQuotex) "Quotex OTC Verified Feed" else providerName,
                    fontSize = 8.sp,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun ChartStyleSwitcher(
    activeStyle: ChartStyle,
    onSwitch: (ChartStyle) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF13131B))
                .border(0.5.dp, GoldBorder, RoundedCornerShape(6.dp))
                .padding(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (activeStyle == ChartStyle.NATIVE_CANDLE) GoldPrimary else Color.Transparent)
                    .clickable { onSwitch(ChartStyle.NATIVE_CANDLE) }
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "NATIVE CANVAS",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (activeStyle == ChartStyle.NATIVE_CANDLE) Color.Black else TextMuted
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (activeStyle == ChartStyle.TRADINGVIEW_WIDGET) GoldPrimary else Color.Transparent)
                    .clickable { onSwitch(ChartStyle.TRADINGVIEW_WIDGET) }
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "TRADINGVIEW WEB",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (activeStyle == ChartStyle.TRADINGVIEW_WIDGET) Color.Black else TextMuted
                )
            }
        }
    }
}

@Composable
private fun LiveIndicatorsRibbon(indicators: com.example.engine.TechnicalAnalysisResult?) {
    if (indicators == null) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        IndicatorPill(name = "EMA 9", value = "%.2f".format(indicators.ema9))
        IndicatorPill(name = "EMA 20", value = "%.2f".format(indicators.ema20))
        IndicatorPill(name = "EMA 50", value = "%.2f".format(indicators.ema50))
        IndicatorPill(name = "RSI 14", value = "%.1f".format(indicators.rsi14), isBullish = indicators.rsi14 > 50)
        IndicatorPill(name = "MACD", value = "%.3f".format(indicators.macdHist), isBullish = indicators.macdHist > 0)
        IndicatorPill(name = "Supertrend", value = if (indicators.isSupertrendBullish) "BULL" else "BEAR", isBullish = indicators.isSupertrendBullish)
        IndicatorPill(name = "SAR", value = if (indicators.isSarBullish) "BULL" else "BEAR", isBullish = indicators.isSarBullish)
        IndicatorPill(name = "ATR", value = "%.4f".format(indicators.atr14))
        IndicatorPill(name = "Stoch", value = "%.1f".format(indicators.stochK))
    }
}

@Composable
private fun IndicatorPill(name: String, value: String, isBullish: Boolean? = null) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF111118))
            .border(0.5.dp, GoldBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(name, fontSize = 8.5.sp, color = TextMuted, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = when (isBullish) {
                    true -> BullishGreen
                    false -> BearishRed
                    null -> GoldAccent
                }
            )
        }
    }
}

@Composable
private fun QuotexNoticeCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF14130F))
            .border(0.8.dp, Color(0xFF423719), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(
                text = "QUOTEX OTC PROTOCOL & DATA INTEGRITY NOTICE",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = GoldSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Quotex Over-The-Counter (OTC) sessions use closed algorithmic quotation models. SD BALOCH QX BOT analyzes authorized market feeds with dedicated tick filters. Signal confluence enforces strict 30/50 confirmation to protect trade capital.",
                fontSize = 8.sp,
                color = Color(0xFFC7C1B4),
                lineHeight = 11.sp
            )
        }
    }
}
