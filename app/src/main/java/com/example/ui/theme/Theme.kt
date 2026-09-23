package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PulsaDarkPrimary,
    onPrimary = PulsaDarkOnPrimary,
    secondary = PulsaDarkSecondary,
    tertiary = PulsaDarkTertiary,
    background = PulsaDarkBackground,
    surface = PulsaDarkSurface,
    surfaceVariant = PulsaDarkSurfaceVariant,
    onSurface = PulsaDarkOnSurface,
    outline = PulsaDarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = PulsaPrimary,
    onPrimary = PulsaOnPrimary,
    secondary = PulsaSecondary,
    tertiary = PulsaTertiary,
    background = PulsaBackground,
    surface = PulsaSurface,
    surfaceVariant = PulsaSurfaceVariant,
    onSurface = PulsaOnSurface,
    outline = PulsaOutline
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to keep our intentional fintech brand colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
