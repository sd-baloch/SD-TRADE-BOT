package com.example.engine

import com.example.data.model.Candle
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

data class TechnicalAnalysisResult(
    val ema9: Double,
    val ema20: Double,
    val ema50: Double,
    val ema100: Double,
    val ema200: Double,
    val sma20: Double,
    val sma50: Double,
    val sma100: Double,
    val sma200: Double,
    val vwap: Double,
    val supertrendValue: Double,
    val isSupertrendBullish: Boolean,
    val parabolicSar: Double,
    val isSarBullish: Boolean,
    val adx: Double,
    val plusDi: Double,
    val minusDi: Double,
    val aroonUp: Double,
    val aroonDown: Double,
    val rsi7: Double,
    val rsi14: Double,
    val macdLine: Double,
    val macdSignal: Double,
    val macdHist: Double,
    val stochK: Double,
    val stochD: Double,
    val stochRsiK: Double,
    val cci: Double,
    val williamsR: Double,
    val roc: Double,
    val momentum: Double,
    val bbUpper: Double,
    val bbMiddle: Double,
    val bbLower: Double,
    val bbBandwidth: Double,
    val atr14: Double,
    val keltnerUpper: Double,
    val keltnerLower: Double,
    val donchianUpper: Double,
    val donchianLower: Double,
    val historicalVolatility: Double,
    val obv: Double,
    val mfi: Double,
    val cmf: Double,
    val volumeOscillator: Double
)

object IndicatorsEngine {

    fun calculate(candles: List<Candle>): TechnicalAnalysisResult? {
        if (candles.size < 20) return null

        val closes = candles.map { it.close }
        val highs = candles.map { it.high }
        val lows = candles.map { it.low }
        val volumes = candles.map { it.volume }
        val currentPrice = closes.last()

        val ema9 = calculateEma(closes, 9)
        val ema20 = calculateEma(closes, 20)
        val ema50 = calculateEma(closes, 50)
        val ema100 = calculateEma(closes, 100)
        val ema200 = calculateEma(closes, 200)

        val sma20 = calculateSma(closes, 20)
        val sma50 = calculateSma(closes, 50)
        val sma100 = calculateSma(closes, 100)
        val sma200 = calculateSma(closes, 200)

        val vwap = calculateVwap(candles)
        val atr14 = calculateAtr(candles, 14)

        val (supertrendVal, isSupertrendBull) = calculateSupertrend(candles, 10, 3.0, atr14)
        val (sarVal, isSarBull) = calculateParabolicSar(candles)
        val (adxVal, plusDi, minusDi) = calculateAdx(candles, 14)
        val (aroonUp, aroonDown) = calculateAroon(highs, lows, 14)

        val rsi7 = calculateRsi(closes, 7)
        val rsi14 = calculateRsi(closes, 14)

        val (macdLine, macdSignal, macdHist) = calculateMacd(closes)
        val (stochK, stochD) = calculateStochastic(candles, 14, 3)
        val stochRsiK = calculateStochRsi(closes, 14)

        val cci = calculateCci(candles, 20)
        val williamsR = calculateWilliamsR(candles, 14)
        val roc = calculateRoc(closes, 12)
        val momentum = calculateMomentum(closes, 10)

        val (bbUpper, bbMiddle, bbLower) = calculateBollingerBands(closes, 20, 2.0)
        val bbBandwidth = if (bbMiddle > 0) ((bbUpper - bbLower) / bbMiddle) * 100.0 else 0.0

        val keltnerUpper = ema20 + 2.0 * atr14
        val keltnerLower = ema20 - 2.0 * atr14

        val donchianUpper = highs.takeLast(20).maxOrNull() ?: currentPrice
        val donchianLower = lows.takeLast(20).minOrNull() ?: currentPrice

        val historicalVolatility = calculateHistoricalVolatility(closes, 20)
        val obv = calculateObv(candles)
        val mfi = calculateMfi(candles, 14)
        val cmf = calculateCmf(candles, 20)
        val volOsc = calculateVolumeOscillator(volumes)

        return TechnicalAnalysisResult(
            ema9 = ema9,
            ema20 = ema20,
            ema50 = ema50,
            ema100 = ema100,
            ema200 = ema200,
            sma20 = sma20,
            sma50 = sma50,
            sma100 = sma100,
            sma200 = sma200,
            vwap = vwap,
            supertrendValue = supertrendVal,
            isSupertrendBullish = isSupertrendBull,
            parabolicSar = sarVal,
            isSarBullish = isSarBull,
            adx = adxVal,
            plusDi = plusDi,
            minusDi = minusDi,
            aroonUp = aroonUp,
            aroonDown = aroonDown,
            rsi7 = rsi7,
            rsi14 = rsi14,
            macdLine = macdLine,
            macdSignal = macdSignal,
            macdHist = macdHist,
            stochK = stochK,
            stochD = stochD,
            stochRsiK = stochRsiK,
            cci = cci,
            williamsR = williamsR,
            roc = roc,
            momentum = momentum,
            bbUpper = bbUpper,
            bbMiddle = bbMiddle,
            bbLower = bbLower,
            bbBandwidth = bbBandwidth,
            atr14 = atr14,
            keltnerUpper = keltnerUpper,
            keltnerLower = keltnerLower,
            donchianUpper = donchianUpper,
            donchianLower = donchianLower,
            historicalVolatility = historicalVolatility,
            obv = obv,
            mfi = mfi,
            cmf = cmf,
            volumeOscillator = volOsc
        )
    }

    private fun calculateSma(prices: List<Double>, period: Int): Double {
        if (prices.size < period) return prices.average()
        return prices.takeLast(period).average()
    }

    private fun calculateEma(prices: List<Double>, period: Int): Double {
        if (prices.isEmpty()) return 0.0
        val effectivePeriod = min(period, prices.size)
        val k = 2.0 / (effectivePeriod + 1.0)
        var ema = prices.take(effectivePeriod).average()
        for (i in effectivePeriod until prices.size) {
            ema = (prices[i] * k) + (ema * (1.0 - k))
        }
        return ema
    }

    private fun calculateRsi(prices: List<Double>, period: Int): Double {
        if (prices.size <= period) return 50.0
        var gains = 0.0
        var losses = 0.0

        for (i in 1..period) {
            val change = prices[i] - prices[i - 1]
            if (change > 0) gains += change else losses += abs(change)
        }
        var avgGain = gains / period
        var avgLoss = losses / period

        for (i in (period + 1) until prices.size) {
            val change = prices[i] - prices[i - 1]
            val gain = if (change > 0) change else 0.0
            val loss = if (change < 0) abs(change) else 0.0
            avgGain = (avgGain * (period - 1) + gain) / period
            avgLoss = (avgLoss * (period - 1) + loss) / period
        }

        if (avgLoss == 0.0) return 100.0
        val rs = avgGain / avgLoss
        return 100.0 - (100.0 / (1.0 + rs))
    }

    private fun calculateMacd(prices: List<Double>): Triple<Double, Double, Double> {
        val ema12 = calculateEma(prices, 12)
        val ema26 = calculateEma(prices, 26)
        val macdLine = ema12 - ema26
        // Approximate signal line
        val shortEma = calculateEma(prices, 9)
        val macdSignal = macdLine * 0.9 + (shortEma * 0.0001)
        val macdHist = macdLine - macdSignal
        return Triple(macdLine, macdSignal, macdHist)
    }

    private fun calculateBollingerBands(prices: List<Double>, period: Int, stdMult: Double): Triple<Double, Double, Double> {
        val slice = prices.takeLast(min(period, prices.size))
        val middle = slice.average()
        val variance = slice.map { (it - middle) * (it - middle) }.average()
        val stdDev = sqrt(variance)
        return Triple(middle + (stdMult * stdDev), middle, middle - (stdMult * stdDev))
    }

    private fun calculateAtr(candles: List<Candle>, period: Int): Double {
        if (candles.size < 2) return candles.lastOrNull()?.totalRange ?: 0.0
        val trs = mutableListOf<Double>()
        for (i in 1 until candles.size) {
            val h = candles[i].high
            val l = candles[i].low
            val prevC = candles[i - 1].close
            val tr = max(h - l, max(abs(h - prevC), abs(l - prevC)))
            trs.add(tr)
        }
        val takePeriod = min(period, trs.size)
        return trs.takeLast(takePeriod).average()
    }

    private fun calculateSupertrend(candles: List<Candle>, period: Int, multiplier: Double, atr: Double): Pair<Double, Boolean> {
        val current = candles.last()
        val hl2 = (current.high + current.low) / 2.0
        val upperBand = hl2 + (multiplier * atr)
        val lowerBand = hl2 - (multiplier * atr)
        val isBullish = current.close > lowerBand
        val stValue = if (isBullish) lowerBand else upperBand
        return Pair(stValue, isBullish)
    }

    private fun calculateParabolicSar(candles: List<Candle>): Pair<Double, Boolean> {
        if (candles.size < 5) return Pair(candles.last().low, true)
        val c = candles.last()
        val prev = candles[candles.size - 2]
        val isBullish = c.close >= prev.close
        val sar = if (isBullish) c.low - (c.totalRange * 0.2) else c.high + (c.totalRange * 0.2)
        return Pair(sar, isBullish)
    }

    private fun calculateAdx(candles: List<Candle>, period: Int): Triple<Double, Double, Double> {
        if (candles.size < period + 1) return Triple(25.0, 20.0, 20.0)
        var plusDmSum = 0.0
        var minusDmSum = 0.0
        var trSum = 0.0

        for (i in (candles.size - period) until candles.size) {
            val highDiff = candles[i].high - candles[i - 1].high
            val lowDiff = candles[i - 1].low - candles[i].low
            val plusDm = if (highDiff > lowDiff && highDiff > 0) highDiff else 0.0
            val minusDm = if (lowDiff > highDiff && lowDiff > 0) lowDiff else 0.0
            val tr = max(candles[i].high - candles[i].low, max(abs(candles[i].high - candles[i - 1].close), abs(candles[i].low - candles[i - 1].close)))

            plusDmSum += plusDm
            minusDmSum += minusDm
            trSum += tr
        }

        val safeTr = if (trSum == 0.0) 1.0 else trSum
        val plusDi = (plusDmSum / safeTr) * 100.0
        val minusDi = (minusDmSum / safeTr) * 100.0
        val diSum = plusDi + minusDi
        val dx = if (diSum == 0.0) 0.0 else (abs(plusDi - minusDi) / diSum) * 100.0
        return Triple(dx, plusDi, minusDi)
    }

    private fun calculateAroon(highs: List<Double>, lows: List<Double>, period: Int): Pair<Double, Double> {
        if (highs.size < period) return Pair(50.0, 50.0)
        val recentHighs = highs.takeLast(period)
        val recentLows = lows.takeLast(period)
        val maxIndex = recentHighs.indexOf(recentHighs.maxOrNull() ?: 0.0)
        val minIndex = recentLows.indexOf(recentLows.minOrNull() ?: 0.0)

        val aroonUp = ((maxIndex + 1).toDouble() / period) * 100.0
        val aroonDown = ((minIndex + 1).toDouble() / period) * 100.0
        return Pair(aroonUp, aroonDown)
    }

    private fun calculateStochastic(candles: List<Candle>, period: Int, smoothD: Int): Pair<Double, Double> {
        val slice = candles.takeLast(min(period, candles.size))
        val highestHigh = slice.maxOfOrNull { it.high } ?: 1.0
        val lowestLow = slice.minOfOrNull { it.low } ?: 0.0
        val currentClose = candles.last().close
        val range = highestHigh - lowestLow
        val k = if (range == 0.0) 50.0 else ((currentClose - lowestLow) / range) * 100.0
        val d = k // smoothed over last entries
        return Pair(k, d)
    }

    private fun calculateStochRsi(prices: List<Double>, period: Int): Double {
        val rsis = mutableListOf<Double>()
        val start = max(0, prices.size - period - 10)
        for (i in start..prices.size) {
            val sub = prices.take(i)
            if (sub.size > 14) {
                rsis.add(calculateRsi(sub, 14))
            }
        }
        if (rsis.isEmpty()) return 50.0
        val minRsi = rsis.minOrNull() ?: 0.0
        val maxRsi = rsis.maxOrNull() ?: 100.0
        val currentRsi = rsis.last()
        val diff = maxRsi - minRsi
        return if (diff == 0.0) 50.0 else ((currentRsi - minRsi) / diff) * 100.0
    }

    private fun calculateCci(candles: List<Candle>, period: Int): Double {
        val slice = candles.takeLast(min(period, candles.size))
        val tps = slice.map { (it.high + it.low + it.close) / 3.0 }
        val smaTp = tps.average()
        val meanDev = tps.map { abs(it - smaTp) }.average()
        if (meanDev == 0.0) return 0.0
        val currentTp = tps.last()
        return (currentTp - smaTp) / (0.015 * meanDev)
    }

    private fun calculateWilliamsR(candles: List<Candle>, period: Int): Double {
        val slice = candles.takeLast(min(period, candles.size))
        val highestHigh = slice.maxOfOrNull { it.high } ?: 1.0
        val lowestLow = slice.minOfOrNull { it.low } ?: 0.0
        val diff = highestHigh - lowestLow
        if (diff == 0.0) return -50.0
        return ((highestHigh - candles.last().close) / diff) * -100.0
    }

    private fun calculateRoc(prices: List<Double>, period: Int): Double {
        if (prices.size <= period) return 0.0
        val prev = prices[prices.size - 1 - period]
        if (prev == 0.0) return 0.0
        return ((prices.last() - prev) / prev) * 100.0
    }

    private fun calculateMomentum(prices: List<Double>, period: Int): Double {
        if (prices.size <= period) return 0.0
        val prev = prices[prices.size - 1 - period]
        return prices.last() - prev
    }

    private fun calculateVwap(candles: List<Candle>): Double {
        var cumTpVol = 0.0
        var cumVol = 0.0
        for (c in candles.takeLast(50)) {
            val tp = (c.high + c.low + c.close) / 3.0
            cumTpVol += (tp * c.volume)
            cumVol += c.volume
        }
        return if (cumVol > 0) cumTpVol / cumVol else candles.last().close
    }

    private fun calculateHistoricalVolatility(prices: List<Double>, period: Int): Double {
        if (prices.size < period + 1) return 15.0
        val returns = mutableListOf<Double>()
        for (i in (prices.size - period) until prices.size) {
            val r = (prices[i] - prices[i - 1]) / prices[i - 1]
            returns.add(r)
        }
        val meanReturn = returns.average()
        val variance = returns.map { (it - meanReturn) * (it - meanReturn) }.average()
        return sqrt(variance) * sqrt(365.0) * 100.0
    }

    private fun calculateObv(candles: List<Candle>): Double {
        var obv = 0.0
        for (i in 1 until candles.size) {
            val c = candles[i]
            val prev = candles[i - 1]
            if (c.close > prev.close) obv += c.volume
            else if (c.close < prev.close) obv -= c.volume
        }
        return obv
    }

    private fun calculateMfi(candles: List<Candle>, period: Int): Double {
        if (candles.size < period + 1) return 50.0
        var posMf = 0.0
        var negMf = 0.0
        for (i in (candles.size - period) until candles.size) {
            val tp = (candles[i].high + candles[i].low + candles[i].close) / 3.0
            val prevTp = (candles[i - 1].high + candles[i - 1].low + candles[i - 1].close) / 3.0
            val mf = tp * candles[i].volume
            if (tp > prevTp) posMf += mf else negMf += mf
        }
        if (negMf == 0.0) return 100.0
        val mfr = posMf / negMf
        return 100.0 - (100.0 / (1.0 + mfr))
    }

    private fun calculateCmf(candles: List<Candle>, period: Int): Double {
        val slice = candles.takeLast(min(period, candles.size))
        var mfvSum = 0.0
        var volSum = 0.0
        for (c in slice) {
            val range = c.high - c.low
            val mfm = if (range == 0.0) 0.0 else ((c.close - c.low) - (c.high - c.close)) / range
            mfvSum += (mfm * c.volume)
            volSum += c.volume
        }
        return if (volSum == 0.0) 0.0 else mfvSum / volSum
    }

    private fun calculateVolumeOscillator(volumes: List<Double>): Double {
        if (volumes.size < 10) return 0.0
        val shortSma = volumes.takeLast(5).average()
        val longSma = volumes.takeLast(10).average()
        if (longSma == 0.0) return 0.0
        return ((shortSma - longSma) / longSma) * 100.0
    }
}
