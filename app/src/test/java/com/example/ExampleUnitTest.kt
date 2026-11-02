package com.example

import com.example.data.model.Candle
import com.example.data.model.PlatformMode
import com.example.data.model.TimeframeOption
import com.example.engine.CandlestickEngine
import com.example.engine.ConfirmationEngine
import com.example.engine.IndicatorsEngine
import com.example.engine.MarketStructureEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testMathematicalIndicators() {
        val candles = (1..40).map { i ->
            val p = 100.0 + (i * 0.5)
            Candle(
                timestamp = 1000L * i,
                open = p,
                high = p + 1.0,
                low = p - 0.5,
                close = p + 0.8,
                volume = 200.0
            )
        }
        val result = IndicatorsEngine.calculate(candles)
        assertNotNull(result)
        assertTrue(result!!.ema9 > 100.0)
        assertTrue(result.rsi14 in 0.0..100.0)
    }

    @Test
    fun testConfirmationEngineEvaluation() {
        val candles = (1..30).map { i ->
            val p = 50.0 + i
            Candle(1000L * i, p, p + 2.0, p - 0.5, p + 1.5, 300.0)
        }
        val indicators = IndicatorsEngine.calculate(candles)
        val pattern = CandlestickEngine.analyze(candles)
        val structure = MarketStructureEngine.analyze(candles)

        val signal = ConfirmationEngine.evaluate(
            symbol = "BTCUSDT",
            platform = PlatformMode.QUOTEX,
            timeframe = TimeframeOption.MIN_1,
            candles = candles,
            indicators = indicators,
            pattern = pattern,
            structure = structure,
            dataSource = "Quotex Verified Feed",
            latencyMs = 45
        )

        assertNotNull(signal)
        assertEquals(35, signal.confirmations.size)
        assertTrue(signal.confidencePercentage >= 0.0)
    }
}
