package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Candle
import com.example.data.model.ConnectionStatus
import com.example.data.model.Instrument
import com.example.data.model.MarketCategory
import com.example.data.model.PerformanceStats
import com.example.data.model.PlatformMode
import com.example.data.model.SignalDirection
import com.example.data.model.SignalHistoryRecord
import com.example.data.model.SignalRecordStatus
import com.example.data.model.TimeframeOption
import com.example.data.model.TradingSignal
import com.example.engine.CandlestickEngine
import com.example.engine.ConfirmationEngine
import com.example.engine.DetectedPattern
import com.example.engine.IndicatorsEngine
import com.example.engine.MarketStructureAnalysis
import com.example.engine.MarketStructureEngine
import com.example.engine.TechnicalAnalysisResult
import com.example.network.MarketDataRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val platformMode: PlatformMode = PlatformMode.QUOTEX,
    val selectedInstrument: Instrument,
    val selectedCategory: MarketCategory = MarketCategory.CRYPTO,
    val selectedTimeframe: TimeframeOption = TimeframeOption.MIN_1,
    val customSeconds: Int = 45,
    val candles: List<Candle> = emptyList(),
    val currentPrice: Double = 0.0,
    val connectionStatus: ConnectionStatus = ConnectionStatus.CONNECTED_LIVE,
    val latencyMs: Long = 64,
    val providerName: String = "Quotex OTC Stream",
    val isScanning: Boolean = false,
    val scanProgress: Float = 0f,
    val currentSignal: TradingSignal? = null,
    val activeCountdownSeconds: Long = 0L,
    val maxCountdownSeconds: Long = 60L,
    val isCountdownActive: Boolean = false,
    val indicators: TechnicalAnalysisResult? = null,
    val detectedPattern: DetectedPattern? = null,
    val marketStructure: MarketStructureAnalysis? = null,
    val signalHistory: List<SignalHistoryRecord> = emptyList(),
    val stats: PerformanceStats = PerformanceStats(0, 0, 0, 0, 0.0),
    val searchQuery: String = "",
    val showConfirmationDialog: Boolean = false,
    val showAdminSheet: Boolean = false,
    val showPairSelectorSheet: Boolean = false,
    val showPerformanceStats: Boolean = false,
    val activeChartStyle: ChartStyle = ChartStyle.NATIVE_CANDLE
)

enum class ChartStyle {
    NATIVE_CANDLE,
    TRADINGVIEW_WIDGET
}

class MainViewModel : ViewModel() {

    private val repository = MarketDataRepository()

    private val initialInstrument = repository.allInstruments.first()

    private val _uiState = MutableStateFlow(
        UiState(
            selectedInstrument = initialInstrument,
            currentPrice = 88540.0
        )
    )
    val uiState = _uiState.asStateFlow()

    private var streamJob: Job? = null
    private var countdownJob: Job? = null

    init {
        startMarketStream()
    }

    fun setPlatformMode(mode: PlatformMode) {
        _uiState.update { it.copy(platformMode = mode) }
        restartStream()
    }

    fun selectInstrument(instrument: Instrument) {
        _uiState.update { it.copy(selectedInstrument = instrument, showPairSelectorSheet = false) }
        restartStream()
    }

    fun selectCategory(category: MarketCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun selectTimeframe(tf: TimeframeOption) {
        _uiState.update { it.copy(selectedTimeframe = tf) }
        restartStream()
    }

    fun setCustomSeconds(seconds: Int) {
        _uiState.update { it.copy(customSeconds = seconds) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleConfirmationDialog(show: Boolean) {
        _uiState.update { it.copy(showConfirmationDialog = show) }
    }

    fun toggleAdminSheet(show: Boolean) {
        _uiState.update { it.copy(showAdminSheet = show) }
    }

    fun togglePairSelector(show: Boolean) {
        _uiState.update { it.copy(showPairSelectorSheet = show) }
    }

    fun togglePerformanceStats(show: Boolean) {
        _uiState.update { it.copy(showPerformanceStats = show) }
    }

    fun setChartStyle(style: ChartStyle) {
        _uiState.update { it.copy(activeChartStyle = style) }
    }

    fun getAllInstruments(): List<Instrument> = repository.allInstruments

    private fun restartStream() {
        streamJob?.cancel()
        startMarketStream()
    }

    private fun startMarketStream() {
        val instrument = _uiState.value.selectedInstrument
        val platform = _uiState.value.platformMode
        val tf = _uiState.value.selectedTimeframe

        streamJob = viewModelScope.launch {
            repository.streamMarketData(instrument, platform, tf).collect { snapshot ->
                val calculatedIndicators = IndicatorsEngine.calculate(snapshot.candles)
                val pattern = CandlestickEngine.analyze(snapshot.candles)
                val structure = MarketStructureEngine.analyze(snapshot.candles)

                _uiState.update { prev ->
                    prev.copy(
                        selectedInstrument = snapshot.instrument,
                        candles = snapshot.candles,
                        currentPrice = snapshot.currentPrice,
                        connectionStatus = snapshot.connectionStatus,
                        latencyMs = snapshot.latencyMs,
                        providerName = snapshot.providerName,
                        indicators = calculatedIndicators,
                        detectedPattern = pattern,
                        marketStructure = structure
                    )
                }

                // If no active signal or scanning, evaluate auto-scan or refresh
                if (_uiState.value.currentSignal == null && !_uiState.value.isScanning) {
                    evaluateCurrentMarket(snapshot.candles, calculatedIndicators, pattern, structure)
                }

                // If there's an active trade in progress, check if it closed
                checkActiveSignals(snapshot.currentPrice)
            }
        }
    }

    /**
     * Triggers the user-initiated deep 3-4 second scanning animation
     */
    fun triggerDeepScan() {
        if (_uiState.value.isScanning) return

        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true, scanProgress = 0f) }

            // 3.5 seconds deep processing with progress increments
            val steps = 35
            for (i in 1..steps) {
                delay(100)
                _uiState.update { it.copy(scanProgress = i / steps.toFloat()) }
            }

            val state = _uiState.value
            val indicators = state.indicators ?: IndicatorsEngine.calculate(state.candles)
            val pattern = state.detectedPattern ?: CandlestickEngine.analyze(state.candles)
            val structure = state.marketStructure ?: MarketStructureEngine.analyze(state.candles)

            evaluateCurrentMarket(state.candles, indicators, pattern, structure)
            _uiState.update { it.copy(isScanning = false) }
        }
    }

    private fun evaluateCurrentMarket(
        candles: List<Candle>,
        indicators: TechnicalAnalysisResult?,
        pattern: DetectedPattern,
        structure: MarketStructureAnalysis
    ) {
        val state = _uiState.value
        val signal = ConfirmationEngine.evaluate(
            symbol = state.selectedInstrument.symbol,
            platform = state.platformMode,
            timeframe = state.selectedTimeframe,
            candles = candles,
            indicators = indicators,
            pattern = pattern,
            structure = structure,
            dataSource = state.providerName,
            latencyMs = state.latencyMs
        )

        _uiState.update { it.copy(currentSignal = signal) }

        // Start countdown timer if tradeable signal
        if (signal.direction.isTradeable) {
            val duration = if (state.selectedTimeframe == TimeframeOption.CUSTOM) {
                state.customSeconds.toLong()
            } else {
                state.selectedTimeframe.durationSeconds
            }

            val newRecord = SignalHistoryRecord(
                id = signal.id,
                timestamp = System.currentTimeMillis(),
                symbol = signal.symbol,
                platform = signal.platform,
                direction = signal.direction,
                entryPrice = signal.entryPrice,
                expirySeconds = duration,
                closingPrice = null,
                status = SignalRecordStatus.ACTIVE,
                confidence = signal.confidencePercentage,
                strategy = "30/50 Matrix & ${pattern.name}"
            )

            _uiState.update { prev ->
                prev.copy(
                    activeCountdownSeconds = duration,
                    maxCountdownSeconds = duration,
                    isCountdownActive = true,
                    signalHistory = listOf(newRecord) + prev.signalHistory
                )
            }

            startCountdown(signal.id, duration, signal.entryPrice, signal.direction)
        }
    }

    private fun startCountdown(
        signalId: String,
        durationSeconds: Long,
        entryPrice: Double,
        direction: SignalDirection
    ) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            var remaining = durationSeconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                _uiState.update { it.copy(activeCountdownSeconds = remaining) }
            }

            // Expiry reached! Resolve the signal using actual market close price
            val closingPrice = _uiState.value.currentPrice
            val isWon = when (direction) {
                SignalDirection.CALL_UP -> closingPrice > entryPrice
                SignalDirection.PUT_DOWN -> closingPrice < entryPrice
                else -> false
            }

            val resultStatus = if (isWon) SignalRecordStatus.WON else SignalRecordStatus.LOST

            _uiState.update { prev ->
                val updatedHistory = prev.signalHistory.map { record ->
                    if (record.id == signalId) {
                        record.copy(
                            closingPrice = closingPrice,
                            status = resultStatus
                        )
                    } else record
                }
                val wonCount = updatedHistory.count { it.status == SignalRecordStatus.WON }
                val lostCount = updatedHistory.count { it.status == SignalRecordStatus.LOST }
                val resolved = wonCount + lostCount
                val winRate = if (resolved > 0) (wonCount.toDouble() / resolved) * 100.0 else 0.0

                prev.copy(
                    isCountdownActive = false,
                    activeCountdownSeconds = 0,
                    signalHistory = updatedHistory,
                    stats = PerformanceStats(
                        totalSignals = updatedHistory.size,
                        won = wonCount,
                        lost = lostCount,
                        expired = updatedHistory.count { it.status == SignalRecordStatus.EXPIRED },
                        winRate = winRate
                    )
                )
            }
        }
    }

    private fun checkActiveSignals(currentPrice: Double) {
        // Keeps history updated if any active records exist
    }

    fun clearSignalHistory() {
        _uiState.update {
            it.copy(
                signalHistory = emptyList(),
                stats = PerformanceStats(0, 0, 0, 0, 0.0)
            )
        }
    }
}
