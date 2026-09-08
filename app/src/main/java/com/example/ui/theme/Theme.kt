package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LollipopLightScheme = lightColorScheme(
    primary = LollipopTeal500,
    onPrimary = Color.White,
    primaryContainer = LollipopTeal700,
    onPrimaryContainer = Color.White,
    secondary = LollipopAmber500,
    onSecondary = Color.Black,
    tertiary = LollipopDeepOrange500,
    onTertiary = Color.White,
    background = MaterialBackgroundLight,
    onBackground = MaterialTextPrimary,
    surface = MaterialCardWhite,
    onSurface = MaterialTextPrimary,
    surfaceVariant = Color(0xFFEEEEEE),
    onSurfaceVariant = MaterialTextSecondary,
    outline = MaterialDivider
)

private val LollipopDarkScheme = darkColorScheme(
    primary = LollipopTeal500,
    onPrimary = Color.White,
    primaryContainer = LollipopTeal900,
    onPrimaryContainer = Color.White,
    secondary = LollipopAmber500,
    onSecondary = Color.Black,
    tertiary = LollipopDeepOrange500,
    onTertiary = Color.White,
    background = LollipopDarkBackground,
    onBackground = LollipopTextDarkPrimary,
    surface = LollipopDarkSurface,
    onSurface = LollipopTextDarkPrimary,
    surfaceVariant = Color(0xFF37474F),
    onSurfaceVariant = LollipopTextDarkSecondary,
    outline = Color(0x33FFFFFF)
)

@Composable
fun HUBlubLauncherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) LollipopDarkScheme else LollipopLightScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias for any existing tests
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    HUBlubLauncherTheme(darkTheme = darkTheme, content = content)
}
