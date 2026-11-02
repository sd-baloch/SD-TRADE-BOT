package com.example.engine

import com.example.data.model.Candle
import com.example.data.model.CheckStatus
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class DetectedPattern(
    val name: String,
    val status: CheckStatus,
    val confidence: Double,
    val description: String
)

object CandlestickEngine {

    fun analyze(candles: List<Candle>): DetectedPattern {
        if (candles.size < 3) {
            return DetectedPattern("Scanning...", CheckStatus.NEUTRAL, 50.0, "Gathering tick data")
        }

        val c = candles.last()
        val prev = candles[candles.size - 2]
        val prev2 = candles[candles.size - 3]

        val body = c.bodySize
        val range = if (c.totalRange > 0) c.totalRange else 0.0001
        val bodyRatio = body / range
        val upperWick = c.upperWick
        val lowerWick = c.lowerWick

        // 1. Hammer (Bullish Pin Bar with long lower shadow in downtrend)
        if (lowerWick >= (2.0 * body) && upperWick <= (0.2 * range) && prev.close < prev2.close) {
            return DetectedPattern(
                name = "Hammer (Bullish Pin Bar)",
                status = CheckStatus.BULLISH,
                confidence = 86.4,
                description = "Strong rejection of lower price levels with 2x lower wick"
            )
        }

        // 2. Shooting Star (Bearish Pin Bar with long upper shadow in uptrend)
        if (upperWick >= (2.0 * body) && lowerWick <= (0.2 * range) && prev.close > prev2.close) {
            return DetectedPattern(
                name = "Shooting Star (Bearish Pin Bar)",
                status = CheckStatus.BEARISH,
                confidence = 85.8,
                description = "Severe buyer exhaustion with long upper wick rejection"
            )
        }

        // 3. Bullish Engulfing
        if (!prev.isBullish && c.isBullish && c.close > prev.open && c.open < prev.close) {
            return DetectedPattern(
                name = "Bullish Engulfing",
                status = CheckStatus.BULLISH,
                confidence = 88.5,
                description = "Bullish body completely engulfs prior bearish candle"
            )
        }

        // 4. Bearish Engulfing
        if (prev.isBullish && !c.isBullish && c.close < prev.open && c.open > prev.close) {
            return DetectedPattern(
                name = "Bearish Engulfing",
                status = CheckStatus.BEARISH,
                confidence = 87.9,
                description = "Bearish body completely engulfs prior bullish candle"
            )
        }

        // 5. Morning Star
        if (!prev2.isBullish && prev.bodySize < (prev2.bodySize * 0.4) && c.isBullish && c.close > (prev2.open + prev2.close) / 2) {
            return DetectedPattern(
                name = "Morning Star",
                status = CheckStatus.BULLISH,
                confidence = 89.2,
                description = "Three-bar reversal pattern confirming bullish accumulation"
            )
        }

        // 6. Evening Star
        if (prev2.isBullish && prev.bodySize < (prev2.bodySize * 0.4) && !c.isBullish && c.close < (prev2.open + prev2.close) / 2) {
            return DetectedPattern(
                name = "Evening Star",
                status = CheckStatus.BEARISH,
                confidence = 89.0,
                description = "Three-bar reversal pattern signaling distribution climax"
            )
        }

        // 7. Three White Soldiers
        if (candles.size >= 3 && prev2.isBullish && prev.isBullish && c.isBullish &&
            c.close > prev.close && prev.close > prev2.close &&
            c.open > prev.open && prev.open > prev2.open
        ) {
            return DetectedPattern(
                name = "Three White Soldiers",
                status = CheckStatus.BULLISH,
                confidence = 91.0,
                description = "Consecutive expanding bullish marubozu candles"
            )
        }

        // 8. Three Black Crows
        if (candles.size >= 3 && !prev2.isBullish && !prev.isBullish && !c.isBullish &&
            c.close < prev.close && prev.close < prev2.close &&
            c.open < prev.open && prev.open < prev2.open
        ) {
            return DetectedPattern(
                name = "Three Black Crows",
                status = CheckStatus.BEARISH,
                confidence = 90.5,
                description = "Consecutive aggressive downward selling candles"
            )
        }

        // 9. Marubozu (Strong directional breakout)
        if (bodyRatio > 0.85) {
            return if (c.isBullish) {
                DetectedPattern(
                    name = "Bullish Marubozu",
                    status = CheckStatus.BULLISH,
                    confidence = 84.0,
                    description = "Solid bullish body with negligible wicks, high momentum"
                )
            } else {
                DetectedPattern(
                    name = "Bearish Marubozu",
                    status = CheckStatus.BEARISH,
                    confidence = 84.0,
                    description = "Solid bearish body with negligible wicks, heavy liquidation"
                )
            }
        }

        // 10. Doji & Dragonfly / Gravestone
        if (bodyRatio < 0.1) {
            if (lowerWick > (0.6 * range)) {
                return DetectedPattern(
                    name = "Dragonfly Doji",
                    status = CheckStatus.BULLISH,
                    confidence = 77.0,
                    description = "Session buyers pushed price back to open from extreme low"
                )
            }
            if (upperWick > (0.6 * range)) {
                return DetectedPattern(
                    name = "Gravestone Doji",
                    status = CheckStatus.BEARISH,
                    confidence = 76.5,
                    description = "Session sellers collapsed price from extreme high"
                )
            }
            return DetectedPattern(
                name = "Standard Doji",
                status = CheckStatus.NEUTRAL,
                confidence = 50.0,
                description = "Indecision candle; market awaiting directional volume push"
            )
        }

        // 11. Inside Bar
        if (c.high < prev.high && c.low > prev.low) {
            val bias = if (prev.isBullish) CheckStatus.BULLISH else CheckStatus.BEARISH
            return DetectedPattern(
                name = "Inside Bar Consolidation",
                status = bias,
                confidence = 68.0,
                description = "Volatility compression inside previous bar's range"
            )
        }

        // Default: Momentum Price Action
        return if (c.isBullish) {
            DetectedPattern(
                name = "Bullish Price Expansion",
                status = CheckStatus.BULLISH,
                confidence = 65.0,
                description = "Upward candle close with positive delta volume"
            )
        } else {
            DetectedPattern(
                name = "Bearish Price Expansion",
                status = CheckStatus.BEARISH,
                confidence = 65.0,
                description = "Downward candle close with negative delta volume"
            )
        }
    }
}
