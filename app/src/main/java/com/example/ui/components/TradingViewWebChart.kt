package com.example.ui.components

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.Instrument
import com.example.ui.theme.GoldBorder

@Composable
fun TradingViewWebChart(
    instrument: Instrument,
    modifier: Modifier = Modifier
) {
    val symbolFormatted = when (instrument.symbol) {
        "BTCUSDT" -> "BINANCE:BTCUSDT"
        "ETHUSDT" -> "BINANCE:ETHUSDT"
        "SOLUSDT" -> "BINANCE:SOLUSDT"
        "XRPUSDT" -> "BINANCE:XRPUSDT"
        "EURUSD" -> "FX:EURUSD"
        "GBPUSD" -> "FX:GBPUSD"
        "USDJPY" -> "FX:USDJPY"
        "XAUUSD" -> "OANDA:XAUUSD"
        else -> "BINANCE:${instrument.symbol}"
    }

    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                body { margin: 0; padding: 0; background-color: #0b0b0b; overflow: hidden; }
                #tradingview_widget { width: 100vw; height: 100vh; }
            </style>
        </head>
        <body>
            <div id="tradingview_widget"></div>
            <script type="text/javascript" src="https://s3.tradingview.com/tv.js"></script>
            <script type="text/javascript">
                new TradingView.widget({
                    "autosize": true,
                    "symbol": "$symbolFormatted",
                    "interval": "1",
                    "timezone": "Etc/UTC",
                    "theme": "dark",
                    "style": "1",
                    "locale": "en",
                    "toolbar_bg": "#0b0b0b",
                    "enable_publishing": false,
                    "hide_top_toolbar": false,
                    "hide_legend": false,
                    "save_image": false,
                    "container_id": "tradingview_widget"
                });
            </script>
        </body>
        </html>
    """.trimIndent()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0B0B0B))
            .border(1.dp, GoldBorder, RoundedCornerShape(12.dp))
            .testTag("tradingview_webview_container")
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    webViewClient = WebViewClient()
                    loadDataWithBaseURL("https://www.tradingview.com", htmlContent, "text/html", "UTF-8", null)
                }
            },
            update = { webView ->
                webView.loadDataWithBaseURL("https://www.tradingview.com", htmlContent, "text/html", "UTF-8", null)
            }
        )
    }
}
