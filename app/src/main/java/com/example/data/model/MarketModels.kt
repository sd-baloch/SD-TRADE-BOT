package com.example.data.model

data class Candle(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double
) {
    val isBullish: Boolean get() = close >= open
    val bodySize: Double get() = kotlin.math.abs(close - open)
    val upperWick: Double get() = high - kotlin.math.max(open, close)
    val lowerWick: Double get() = kotlin.math.min(open, close) - low
    val totalRange: Double get() = high - low
}

enum class MarketCategory(val label: String) {
    CRYPTO("Crypto"),
    FOREX("Forex"),
    COMMODITIES("Commodities"),
    INDICES("Indices")
}

data class Instrument(
    val symbol: String,
    val displayName: String,
    val category: MarketCategory,
    val provider: String,
    val decimals: Int = 2,
    val isFavorite: Boolean = false,
    val currentPrice: Double = 0.0,
    val priceChange24h: Double = 0.0,
    val volume24h: Double = 0.0,
    val lastUpdated: Long = System.currentTimeMillis()
)

enum class PlatformMode(val title: String, val shortDesc: String) {
    QUOTEX(
        title = "QUOTEX ANALYSIS",
        shortDesc = "Specialized OTC & High-Frequency Binary Market Analysis"
    ),
    TRADINGVIEW(
        title = "TRADINGVIEW ANALYSIS",
        shortDesc = "Institutional Spot & Multi-Factor Market Structure"
    )
}

enum class TimeframeOption(val label: String, val durationSeconds: Long) {
    SEC_5("5 SEC", 5),
    SEC_30("30 SEC", 30),
    MIN_1("1 MIN", 60),
    MIN_5("5 MIN", 300),
    MIN_10("10 MIN", 600),
    MIN_15("15 MIN", 900),
    CUSTOM("CUSTOM", 45);

    companion object {
        fun fromLabel(label: String): TimeframeOption =
            entries.firstOrNull { it.label == label } ?: MIN_1
    }
}

enum class ConnectionStatus(val label: String, val isOperational: Boolean) {
    CONNECTED_LIVE("LIVE CONNECTED", true),
    DATA_DELAYED("DELAYED MARKET DATA", true),
    DISCONNECTED("LIVE DATA CONNECTION LOST", false),
    UNAVAILABLE("DATA UNAVAILABLE", false)
}
