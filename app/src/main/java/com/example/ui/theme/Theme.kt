package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LuxuryDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.Black,
    primaryContainer = GoldSurface,
    onPrimaryContainer = GoldAccent,
    secondary = GoldSecondary,
    onSecondary = Color.Black,
    secondaryContainer = SurfaceCard,
    onSecondaryContainer = GoldPrimary,
    tertiary = BullishGreen,
    onTertiary = Color.Black,
    background = BackgroundBlack,
    onBackground = TextPrimaryGold,
    surface = SurfaceDark,
    onSurface = TextPrimaryGold,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextMuted,
    outline = GoldBorder,
    outlineVariant = GoldBorderLight,
    error = BearishRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LuxuryDarkColorScheme,
        typography = Typography,
        content = content
    )
}
