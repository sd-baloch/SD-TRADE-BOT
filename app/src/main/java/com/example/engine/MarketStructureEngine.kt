package com.example.engine

import com.example.data.model.Candle
import com.example.data.model.CheckStatus
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class MarketStructureAnalysis(
    val trend: String,
    val structureType: String,
    val isBOS: Boolean,
    val isCHoCH: Boolean,
    val hasFVG: Boolean,
    val hasOrderBlock: Boolean,
    val supportLevel: Double,
    val resistanceLevel: Double,
    val currentSwingPoint: String,
    val structureBias: CheckStatus,
    val summary: String
)

object MarketStructureEngine {

    fun analyze(candles: List<Candle>): MarketStructureAnalysis {
        if (candles.size < 10) {
            val p = candles.lastOrNull()?.close ?: 0.0
            return MarketStructureAnalysis(
                trend = "Equilibrium",
                structureType = "Consolidation",
                isBOS = false,
                isCHoCH = false,
                hasFVG = false,
                hasOrderBlock = false,
                supportLevel = p * 0.99,
                resistanceLevel = p * 1.01,
                currentSwingPoint = "N/A",
                structureBias = CheckStatus.NEUTRAL,
                summary = "Accumulating structural swing points"
            )
        }

        // Identify local swing highs and swing lows (lookback 3)
        val swingHighs = mutableListOf<Pair<Int, Double>>()
        val swingLows = mutableListOf<Pair<Int, Double>>()

        for (i in 2 until (candles.size - 2)) {
            val h = candles[i].high
            val l = candles[i].low
            if (h > candles[i - 1].high && h > candles[i - 2].high && h > candles[i + 1].high && h > candles[i + 2].high) {
                swingHighs.add(Pair(i, h))
            }
            if (l < candles[i - 1].low && l < candles[i - 2].low && l < candles[i + 1].low && l < candles[i + 2].low) {
                swingLows.add(Pair(i, l))
            }
        }

        val currentClose = candles.last().close
        val recentHigh = swingHighs.lastOrNull()?.second ?: candles.takeLast(10).maxOf { it.high }
        val recentLow = swingLows.lastOrNull()?.second ?: candles.takeLast(10).minOf { it.low }

        val supportLevel = recentLow
        val resistanceLevel = recentHigh

        // Check HH, HL vs LH, LL
        var isHigherHigh = false
        var isHigherLow = false
        var isLowerHigh = false
        var isLowerLow = false

        if (swingHighs.size >= 2) {
            val lastSh = swingHighs[swingHighs.size - 1].second
            val prevSh = swingHighs[swingHighs.size - 2].second
            if (lastSh > prevSh) isHigherHigh = true else isLowerHigh = true
        }

        if (swingLows.size >= 2) {
            val lastSl = swingLows[swingLows.size - 1].second
            val prevSl = swingLows[swingLows.size - 2].second
            if (lastSl > prevSl) isHigherLow = true else isLowerLow = true
        }

        // Break of Structure (BOS): price broke above recent swing high or below recent swing low
        val isBullishBOS = currentClose > recentHigh
        val isBearishBOS = currentClose < recentLow
        val isBOS = isBullishBOS || isBearishBOS

        // Change of Character (CHoCH): Trend reversal after consecutive LH or HL
        val isBullishCHoCH = isLowerHigh && currentClose > recentHigh
        val isBearishCHoCH = isHigherLow && currentClose < recentLow
        val isCHoCH = isBullishCHoCH || isBearishCHoCH

        // Fair Value Gap (FVG): candle 3 low > candle 1 high (Bullish FVG) or candle 3 high < candle 1 low (Bearish FVG)
        val hasFVG = if (candles.size >= 3) {
            val c1 = candles[candles.size - 3]
            val c3 = candles.last()
            (c3.low > c1.high) || (c3.high < c1.low)
        } else false

        // Order Block (last opposite candle before an energetic move)
        val hasOrderBlock = abs(candles.last().close - candles.last().open) > (candles.takeLast(5).map { it.bodySize }.average() * 1.5)

        val swingLabel = when {
            isHigherHigh && isHigherLow -> "HH + HL (Bullish Extension)"
            isLowerHigh && isLowerLow -> "LH + LL (Bearish Liquidation)"
            isHigherHigh -> "Higher High (Liquidity Sweep)"
            isLowerLow -> "Lower Low (Sell Stop Run)"
            else -> "Trading Range Equilibrium"
        }

        val bias = when {
            isBullishCHoCH || (isBullishBOS && isHigherHigh) -> CheckStatus.BULLISH
            isBearishCHoCH || (isBearishBOS && isLowerLow) -> CheckStatus.BEARISH
            isHigherLow && currentClose > ((recentHigh + recentLow) / 2) -> CheckStatus.BULLISH
            isLowerHigh && currentClose < ((recentHigh + recentLow) / 2) -> CheckStatus.BEARISH
            else -> CheckStatus.NEUTRAL
        }

        val trend = when (bias) {
            CheckStatus.BULLISH -> "Bullish Expansion"
            CheckStatus.BEARISH -> "Bearish Distribution"
            else -> "Sideways Range"
        }

        val structureType = when {
            isCHoCH -> "CHoCH (Character Change Reversal)"
            isBOS -> "BOS (Break of Structure)"
            hasFVG -> "FVG Imbalance Zone"
            else -> "Market Structure Order Flow"
        }

        val summary = when (bias) {
            CheckStatus.BULLISH -> "Price holding above support at ${"%.5f".format(supportLevel)}, targeting liquidity above ${"%.5f".format(resistanceLevel)}"
            CheckStatus.BEARISH -> "Price rejected at resistance ${"%.5f".format(resistanceLevel)}, sweeping sell-side liquidity near ${"%.5f".format(supportLevel)}"
            else -> "Market oscillating between S=${"%.5f".format(supportLevel)} and R=${"%.5f".format(resistanceLevel)}"
        }

        return MarketStructureAnalysis(
            trend = trend,
            structureType = structureType,
            isBOS = isBOS,
            isCHoCH = isCHoCH,
            hasFVG = hasFVG,
            hasOrderBlock = hasOrderBlock,
            supportLevel = supportLevel,
            resistanceLevel = resistanceLevel,
            currentSwingPoint = swingLabel,
            structureBias = bias,
            summary = summary
        )
    }
}
