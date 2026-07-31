package com.example.nexusbi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Minimalist Refined Color Palette
val EmeraldPrimary = Color(0xFF0D9488) // Modern sleek Teal/Emerald
val EmeraldDark = Color(0xFF0F766E)
val EmeraldLight = Color(0xFFF0FDF4)
val TerracottaGold = Color(0xFFD97706)
val SoftSand = Color(0xFFFAFAFA) // Pure clean minimalist background
val PureWhite = Color(0xFFFFFFFF)
val SlateDark = Color(0xFF111827) // High contrast charcoal text
val SlateGray = Color(0xFF6B7280) // Muted subtle text
val LightBorder = Color(0xFFE5E7EB) // Subtle 1dp border line
val RedNegative = Color(0xFFEF4444)
val GreenPositive = Color(0xFF10B981)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = PureWhite,
    primaryContainer = EmeraldLight,
    onPrimaryContainer = EmeraldDark,
    secondary = TerracottaGold,
    onSecondary = PureWhite,
    background = SoftSand,
    onBackground = SlateDark,
    surface = PureWhite,
    onSurface = SlateDark,
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = SlateDark,
    outline = LightBorder,
    error = RedNegative
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF2DD4BF),
    onPrimary = Color(0xFF042F2E),
    primaryContainer = Color(0xFF115E59),
    onPrimaryContainer = Color(0xFFCCFBF1),
    secondary = Color(0xFFFBBF24),
    onSecondary = Color(0xFF451A03),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    outline = Color(0xFF334155)
)

@Composable
fun NexusBITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
