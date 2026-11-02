package com.example.data.model

enum class SignalDirection(val label: String, val actionTitle: String) {
    CALL_UP("CALL / UP", "CALL (BUY)"),
    PUT_DOWN("PUT / DOWN", "PUT (SELL)"),
    WAIT_CONFIRMATION("WAIT FOR CONFIRMATION", "NO VALID SIGNAL");

    val isTradeable: Boolean get() = this == CALL_UP || this == PUT_DOWN
}

enum class CheckStatus(val label: String) {
    BULLISH("BULLISH"),
    BEARISH("BEARISH"),
    NEUTRAL("NEUTRAL"),
    UNAVAILABLE("UNAVAILABLE")
}

data class ConfirmationCheck(
    val id: Int,
    val name: String,
    val category: String, // e.g. "Trend", "Momentum", "Volatility", "Volume", "Price Action", "Market Structure"
    val status: CheckStatus,
    val weight: Double,
    val valueStr: String,
    val detail: String
)

data class TradingSignal(
    val id: String = java.util.UUID.randomUUID().toString(),
    val symbol: String,
    val platform: PlatformMode,
    val direction: SignalDirection,
    val entryPrice: Double,
    val confidencePercentage: Double,
    val timeframe: TimeframeOption,
    val expirySeconds: Long,
    val generatedAt: Long = System.currentTimeMillis(),
    val indicatorsSummary: String,
    val candlePattern: String,
    val marketStructure: String,
    val aiRationale: String,
    val confirmations: List<ConfirmationCheck>,
    val bullishScore: Double,
    val bearishScore: Double,
    val dataSource: String,
    val latencyMs: Long = 85
)

enum class SignalRecordStatus {
    ACTIVE,
    WON,
    LOST,
    EXPIRED
}

data class SignalHistoryRecord(
    val id: String,
    val timestamp: Long,
    val symbol: String,
    val platform: PlatformMode,
    val direction: SignalDirection,
    val entryPrice: Double,
    val expirySeconds: Long,
    val closingPrice: Double? = null,
    val status: SignalRecordStatus = SignalRecordStatus.ACTIVE,
    val confidence: Double,
    val strategy: String
)

data class PerformanceStats(
    val totalSignals: Int,
    val won: Int,
    val lost: Int,
    val expired: Int,
    val winRate: Double
)
