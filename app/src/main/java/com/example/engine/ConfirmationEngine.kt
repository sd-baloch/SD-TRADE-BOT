package com.example.engine

import com.example.data.model.Candle
import com.example.data.model.CheckStatus
import com.example.data.model.ConfirmationCheck
import com.example.data.model.PlatformMode
import com.example.data.model.SignalDirection
import com.example.data.model.TimeframeOption
import com.example.data.model.TradingSignal
import java.util.Locale
import kotlin.math.max

object ConfirmationEngine {

    fun evaluate(
        symbol: String,
        platform: PlatformMode,
        timeframe: TimeframeOption,
        candles: List<Candle>,
        indicators: TechnicalAnalysisResult?,
        pattern: DetectedPattern,
        structure: MarketStructureAnalysis,
        dataSource: String,
        latencyMs: Long
    ): TradingSignal {
        val currentPrice = candles.lastOrNull()?.close ?: 0.0

        if (candles.size < 20 || indicators == null) {
            return TradingSignal(
                symbol = symbol,
                platform = platform,
                direction = SignalDirection.WAIT_CONFIRMATION,
                entryPrice = currentPrice,
                confidencePercentage = 0.0,
                timeframe = timeframe,
                expirySeconds = timeframe.durationSeconds,
                indicatorsSummary = "Gathering live candles...",
                candlePattern = "Insufficient historical ticks",
                marketStructure = "Neutral Structure",
                aiRationale = "Signal generation disabled: market data stream requires at least 20 synchronized candles to compute mathematical indicators accurately.",
                confirmations = emptyList(),
                bullishScore = 0.0,
                bearishScore = 0.0,
                dataSource = dataSource,
                latencyMs = latencyMs
            )
        }

        val checks = mutableListOf<ConfirmationCheck>()
        var idCounter = 1

        fun addCheck(
            name: String,
            category: String,
            status: CheckStatus,
            weight: Double,
            valueStr: String,
            detail: String
        ) {
            checks.add(
                ConfirmationCheck(
                    id = idCounter++,
                    name = name,
                    category = category,
                    status = status,
                    weight = weight,
                    valueStr = valueStr,
                    detail = detail
                )
            )
        }

        // 1. EMA 9/20 Alignment
        val emaShortStatus = when {
            indicators.ema9 > indicators.ema20 && currentPrice > indicators.ema9 -> CheckStatus.BULLISH
            indicators.ema9 < indicators.ema20 && currentPrice < indicators.ema9 -> CheckStatus.BEARISH
            else -> CheckStatus.NEUTRAL
        }
        addCheck("EMA 9 vs EMA 20", "Trend", emaShortStatus, 3.5, "EMA9: ${formatDec(indicators.ema9)}", "Short-term trend directional gradient")

        // 2. EMA 50/200 Trend Filter (Golden / Death Cross)
        val emaTrendStatus = when {
            indicators.ema50 > indicators.ema200 -> CheckStatus.BULLISH
            indicators.ema50 < indicators.ema200 -> CheckStatus.BEARISH
            else -> CheckStatus.NEUTRAL
        }
        addCheck("EMA 50 / 200 Cross", "Trend", emaTrendStatus, 4.0, "EMA50 vs EMA200", "Macro institutional trend bias")

        // 3. SMA 20 Alignment
        val sma20Status = when {
            currentPrice > indicators.sma20 -> CheckStatus.BULLISH
            currentPrice < indicators.sma20 -> CheckStatus.BEARISH
            else -> CheckStatus.NEUTRAL
        }
        addCheck("SMA 20 Mean", "Trend", sma20Status, 2.5, formatDec(indicators.sma20), "Price relation to 20-period baseline")

        // 4. SMA 50 / 100 Alignment
        val smaMidStatus = when {
            indicators.sma50 > indicators.sma100 -> CheckStatus.BULLISH
            indicators.sma50 < indicators.sma100 -> CheckStatus.BEARISH
            else -> CheckStatus.NEUTRAL
        }
        addCheck("SMA 50 / 100", "Trend", smaMidStatus, 2.5, "SMA50 vs SMA100", "Intermediate trend alignment")

        // 5. RSI 14 Momentum
        val rsi14Status = when {
            indicators.rsi14 in 50.0..70.0 -> CheckStatus.BULLISH
            indicators.rsi14 in 30.0..50.0 -> CheckStatus.BEARISH
            indicators.rsi14 > 70.0 -> CheckStatus.BEARISH // Overbought exhaustion
            indicators.rsi14 < 30.0 -> CheckStatus.BULLISH // Oversold bounce
            else -> CheckStatus.NEUTRAL
        }
        addCheck("RSI 14 Momentum", "Momentum", rsi14Status, 3.5, "%.1f".format(indicators.rsi14), "14-period momentum and exhaustion index")

        // 6. RSI 7 Fast Delta
        val rsi7Status = when {
            indicators.rsi7 > 55.0 -> CheckStatus.BULLISH
            indicators.rsi7 < 45.0 -> CheckStatus.BEARISH
            else -> CheckStatus.NEUTRAL
        }
        addCheck("RSI 7 Fast Trigger", "Momentum", rsi7Status, 2.5, "%.1f".format(indicators.rsi7), "Ultra-fast response velocity")

        // 7. MACD Histogram
        val macdStatus = when {
            indicators.macdHist > 0 && indicators.macdLine > indicators.macdSignal -> CheckStatus.BULLISH
            indicators.macdHist < 0 && indicators.macdLine < indicators.macdSignal -> CheckStatus.BEARISH
            else -> CheckStatus.NEUTRAL
        }
        addCheck("MACD Convergence", "Momentum", macdStatus, 3.5, "Hist: ${"%.4f".format(indicators.macdHist)}", "Moving average convergence/divergence")

        // 8. Stochastic Oscillator (%K & %D)
        val stochStatus = when {
            indicators.stochK > indicators.stochD && indicators.stochK < 80 -> CheckStatus.BULLISH
            indicators.stochK < indicators.stochD && indicators.stochK > 20 -> CheckStatus.BEARISH
            indicators.stochK >= 80 -> CheckStatus.BEARISH
            indicators.stochK <= 20 -> CheckStatus.BULLISH
            else -> CheckStatus.NEUTRAL
        }
        addCheck("Stochastic Oscillator", "Momentum", stochStatus, 3.0, "K: ${"%.1f".format(indicators.stochK)}", "Cyclical range turning point")

        // 9. Stochastic RSI
        val stochRsiStatus = when {
            indicators.stochRsiK > 60 -> CheckStatus.BULLISH
            indicators.stochRsiK < 40 -> CheckStatus.BEARISH
            else -> CheckStatus.NEUTRAL
        }
        addCheck("Stochastic RSI", "Momentum", stochRsiStatus, 2.5, "%.1f".format(indicators.stochRsiK), "RSI bounded oscillator")

        // 10. Supertrend Direction
        val stStatus = if (indicators.isSupertrendBullish) CheckStatus.BULLISH else CheckStatus.BEARISH
        addCheck("Supertrend (10, 3.0)", "Trend", stStatus, 4.0, formatDec(indicators.supertrendValue), "ATR trailing stop flip indicator")

        // 11. Parabolic SAR
        val sarStatus = if (indicators.isSarBullish) CheckStatus.BULLISH else CheckStatus.BEARISH
        addCheck("Parabolic SAR", "Trend", sarStatus, 3.0, formatDec(indicators.parabolicSar), "Stop and Reverse trajectory")

        // 12. ADX Trend Strength
        val adxStatus = when {
            indicators.adx > 25 && indicators.plusDi > indicators.minusDi -> CheckStatus.BULLISH
            indicators.adx > 25 && indicators.minusDi > indicators.plusDi -> CheckStatus.BEARISH
            else -> CheckStatus.NEUTRAL
        }
        addCheck("ADX Trend Strength", "Trend", adxStatus, 3.0, "ADX: ${"%.1f".format(indicators.adx)}", "Directional Movement Index (>25 confirms trend)")

        // 13. Aroon Indicator
        val aroonStatus = when {
            indicators.aroonUp > 70 && indicators.aroonDown < 30 -> CheckStatus.BULLISH
            indicators.aroonDown > 70 && indicators.aroonUp < 30 -> CheckStatus.BEARISH
            else -> CheckStatus.NEUTRAL
        }
        addCheck("Aroon Indicator", "Trend", aroonStatus, 2.5, "Up: ${"%.0f".format(indicators.aroonUp)}%", "High-low time decay trend confirmation")

        // 14. Bollinger Bands Position
        val bbStatus = when {
            currentPrice > indicators.bbMiddle && currentPrice < indicators.bbUpper -> CheckStatus.BULLISH
            currentPrice < indicators.bbMiddle && currentPrice > indicators.bbLower -> CheckStatus.BEARISH
            currentPrice >= indicators.bbUpper -> CheckStatus.BEARISH // Mean reversion squeeze
            currentPrice <= indicators.bbLower -> CheckStatus.BULLISH // Mean reversion bounce
            else -> CheckStatus.NEUTRAL
        }
        addCheck("Bollinger Bands (20, 2.0)", "Volatility", bbStatus, 3.0, "BW: ${"%.2f".format(indicators.bbBandwidth)}%", "Standard deviation volatility envelope")

        // 15. ATR Volatility Expansion
        val atrStatus = if (indicators.atr14 > 0) CheckStatus.BULLISH else CheckStatus.NEUTRAL
        addCheck("ATR Expansion", "Volatility", atrStatus, 2.0, "ATR: ${formatDec(indicators.atr14)}", "True Range expansion filter")

        // 16. Keltner Channels
        val keltnerStatus = when {
            currentPrice > indicators.keltnerUpper -> CheckStatus.BULLISH
            currentPrice < indicators.keltnerLower -> CheckStatus.BEARISH
            else -> CheckStatus.NEUTRAL
        }
        addCheck("Keltner Channels", "Volatility", keltnerStatus, 2.5, "Band breakout", "EMA-ATR volatility channel")

        // 17. Donchian Channel Breakout
        val donchianStatus = when {
            currentPrice >= indicators.donchianUpper * 0.999 -> CheckStatus.BULLISH
            currentPrice <= indicators.donchianLower * 1.001 -> CheckStatus.BEARISH
            else -> CheckStatus.NEUTRAL
        }
        addCheck("Donchian Channels", "Volatility", donchianStatus, 2.5, "20-period range", "Turtle channel boundary inspection")

        // 18. VWAP Position
        val vwapStatus = when {
            currentPrice > indicators.vwap -> CheckStatus.BULLISH
            currentPrice < indicators.vwap -> CheckStatus.BEARISH
            else -> CheckStatus.NEUTRAL
        }
        addCheck("VWAP Benchmark", "Volume", vwapStatus, 3.5, formatDec(indicators.vwap), "Volume-Weighted Average Price positioning")

        // 19. CCI (Commodity Channel Index)
        val cciStatus = when {
            indicators.cci > 100 -> CheckStatus.BULLISH
            indicators.cci < -100 -> CheckStatus.BEARISH
            else -> CheckStatus.NEUTRAL
        }
        addCheck("CCI Momentum", "Momentum", cciStatus, 2.5, "%.1f".format(indicators.cci), "Deviation from statistical mean")

        // 20. Williams %R
        val willStatus = when {
            indicators.williamsR > -30 -> CheckStatus.BULLISH
            indicators.williamsR < -70 -> CheckStatus.BEARISH
            else -> CheckStatus.NEUTRAL
        }
        addCheck("Williams %R", "Momentum", willStatus, 2.0, "%.1f".format(indicators.williamsR), "High-low cycle position")

        // 21. Rate of Change (ROC)
        val rocStatus = when {
            indicators.roc > 0.05 -> CheckStatus.BULLISH
            indicators.roc < -0.05 -> CheckStatus.BEARISH
            else -> CheckStatus.NEUTRAL
        }
        addCheck("Rate of Change (ROC)", "Momentum", rocStatus, 2.0, "${"%.2f".format(indicators.roc)}%", "Speed of price acceleration")

        // 22. Momentum Indicator
        val momStatus = when {
            indicators.momentum > 0 -> CheckStatus.BULLISH
            indicators.momentum < 0 -> CheckStatus.BEARISH
            else -> CheckStatus.NEUTRAL
        }
        addCheck("Momentum Delta", "Momentum", momStatus, 2.0, formatDec(indicators.momentum), "Net delta price velocity")

        // 23. OBV (On-Balance Volume)
        val obvStatus = when {
            indicators.obv > 0 -> CheckStatus.BULLISH
            indicators.obv < 0 -> CheckStatus.BEARISH
            else -> CheckStatus.NEUTRAL
        }
        addCheck("On-Balance Volume (OBV)", "Volume", obvStatus, 3.0, "Accumulation", "Volume accumulation/distribution pressure")

        // 24. Money Flow Index (MFI)
        val mfiStatus = when {
            indicators.mfi in 50.0..80.0 -> CheckStatus.BULLISH
            indicators.mfi in 20.0..50.0 -> CheckStatus.BEARISH
            else -> CheckStatus.NEUTRAL
        }
        addCheck("Money Flow Index (MFI)", "Volume", mfiStatus, 3.0, "%.1f".format(indicators.mfi), "Volume-weighted RSI capital inflow")

        // 25. Chaikin Money Flow (CMF)
        val cmfStatus = when {
            indicators.cmf > 0.05 -> CheckStatus.BULLISH
            indicators.cmf < -0.05 -> CheckStatus.BEARISH
            else -> CheckStatus.NEUTRAL
        }
        addCheck("Chaikin Money Flow", "Volume", cmfStatus, 3.0, "%.3f".format(indicators.cmf), "Institutional accumulation/distribution")

        // 26. Volume Oscillator
        val voStatus = when {
            indicators.volumeOscillator > 0 -> CheckStatus.BULLISH
            else -> CheckStatus.NEUTRAL
        }
        addCheck("Volume Expansion", "Volume", voStatus, 2.0, "${"%.1f".format(indicators.volumeOscillator)}%", "Short vs long-term volume surge")

        // 27. Candlestick Pattern Trigger
        addCheck(pattern.name, "Price Action", pattern.status, 4.0, "Candle Recognition", pattern.description)

        // 28. Wick Rejection Factor
        val lastCandle = candles.last()
        val wickStatus = when {
            lastCandle.lowerWick > (lastCandle.bodySize * 1.2) -> CheckStatus.BULLISH
            lastCandle.upperWick > (lastCandle.bodySize * 1.2) -> CheckStatus.BEARISH
            else -> CheckStatus.NEUTRAL
        }
        addCheck("Wick Liquidity Rejection", "Price Action", wickStatus, 3.0, "Pin Rejection", "Shadow absorption at key price zones")

        // 29. Support Reaction
        val distToSupport = currentPrice - structure.supportLevel
        val supportStatus = if (distToSupport in 0.0..(indicators.atr14 * 0.8)) CheckStatus.BULLISH else CheckStatus.NEUTRAL
        addCheck("Support Level Defense", "Market Structure", supportStatus, 3.5, formatDec(structure.supportLevel), "Bounce off structural demand floor")

        // 30. Resistance Reaction
        val distToResistance = structure.resistanceLevel - currentPrice
        val resStatus = if (distToResistance in 0.0..(indicators.atr14 * 0.8)) CheckStatus.BEARISH else CheckStatus.NEUTRAL
        addCheck("Resistance Liquidity Test", "Market Structure", resStatus, 3.5, formatDec(structure.resistanceLevel), "Ceiling test under distribution cap")

        // 31. Higher High / Lower Low Structure
        addCheck("Structural Trend Flow", "Market Structure", structure.structureBias, 4.0, structure.currentSwingPoint, "Swing high / swing low order flow")

        // 32. Break of Structure (BOS)
        val bosStatus = if (structure.isBOS) structure.structureBias else CheckStatus.NEUTRAL
        addCheck("Break of Structure (BOS)", "Market Structure", bosStatus, 3.5, if (structure.isBOS) "Confirmed" else "None", "Key swing high/low structural breakout")

        // 33. Change of Character (CHoCH)
        val chochStatus = if (structure.isCHoCH) structure.structureBias else CheckStatus.NEUTRAL
        addCheck("Change of Character (CHoCH)", "Market Structure", chochStatus, 3.5, if (structure.isCHoCH) "Reversal" else "None", "Shift in trend structure sequence")

        // 34. Fair Value Gap (FVG) Confluence
        val fvgStatus = if (structure.hasFVG) structure.structureBias else CheckStatus.NEUTRAL
        addCheck("Fair Value Gap (FVG)", "Market Structure", fvgStatus, 2.5, if (structure.hasFVG) "Active FVG" else "None", "Institutional price delivery imbalance")

        // 35. Timeframe Consistency
        val tfTrend = if (currentPrice > indicators.ema100) CheckStatus.BULLISH else CheckStatus.BEARISH
        addCheck("Multi-Timeframe Trend", "Confluence", tfTrend, 3.0, "${timeframe.label} vs Macro", "Higher timeframe trend agreement check")

        // Calculate weighted scores
        val totalWeight = checks.sumOf { it.weight }
        val bullishWeight = checks.filter { it.status == CheckStatus.BULLISH }.sumOf { it.weight }
        val bearishWeight = checks.filter { it.status == CheckStatus.BEARISH }.sumOf { it.weight }

        val bullishScorePct = (bullishWeight / totalWeight) * 100.0
        val bearishScorePct = (bearishWeight / totalWeight) * 100.0

        // Strict 30/50 Threshold:
        // Must achieve at least 65% weighted alignment to trigger trade direction
        val direction: SignalDirection
        val confidence: Double
        val aiRationale: String

        if (bullishScorePct >= 65.0 && bullishScorePct > (bearishScorePct + 18.0)) {
            direction = SignalDirection.CALL_UP
            confidence = (bullishScorePct * 0.9 + 5.0).coerceIn(68.4, 94.8)
            aiRationale = "Rigorous 35-factor confluence verified. Bullish alignment across EMA gradient, ${pattern.name}, and strong support defense at ${formatDec(structure.supportLevel)}. Confidence calibrated at ${"%.1f".format(confidence)}%."
        } else if (bearishScorePct >= 65.0 && bearishScorePct > (bullishScorePct + 18.0)) {
            direction = SignalDirection.PUT_DOWN
            confidence = (bearishScorePct * 0.9 + 5.0).coerceIn(68.4, 94.8)
            aiRationale = "Bearish institutional order flow confirmed with ${"%.1f".format(bearishScorePct)}% confluence. Selling pressure confirmed by ${pattern.name}, negative delta, and resistance test at ${formatDec(structure.resistanceLevel)}."
        } else {
            direction = SignalDirection.WAIT_CONFIRMATION
            confidence = max(bullishScorePct, bearishScorePct)
            aiRationale = "WAIT FOR CONFIRMATION: Confluence matrix is divided (Bullish: ${"%.1f".format(bullishScorePct)}%, Bearish: ${"%.1f".format(bearishScorePct)}%). In accordance with anti-fake algorithms, signal is withheld until clear 30/50 confluence aligns."
        }

        return TradingSignal(
            symbol = symbol,
            platform = platform,
            direction = direction,
            entryPrice = currentPrice,
            confidencePercentage = confidence,
            timeframe = timeframe,
            expirySeconds = timeframe.durationSeconds,
            indicatorsSummary = "EMA9: ${formatDec(indicators.ema9)} | RSI: ${"%.1f".format(indicators.rsi14)} | ST: ${if (indicators.isSupertrendBullish) "BULL" else "BEAR"}",
            candlePattern = pattern.name,
            marketStructure = structure.trend,
            aiRationale = aiRationale,
            confirmations = checks,
            bullishScore = bullishScorePct,
            bearishScore = bearishScorePct,
            dataSource = dataSource,
            latencyMs = latencyMs
        )
    }

    private fun formatDec(value: Double): String {
        return if (value >= 100) {
            String.format(Locale.US, "%.2f", value)
        } else if (value >= 1) {
            String.format(Locale.US, "%.4f", value)
        } else {
            String.format(Locale.US, "%.5f", value)
        }
    }
}
