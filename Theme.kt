package com.techmaina.visionintel.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.techmaina.visionintel.data.settings.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = VisionIntelBlue,
    onPrimary = Color.White,
    secondary = VisionIntelBlueSoft,
    background = VisionIntelBackgroundDark,
    surface = VisionIntelSurfaceDark,
    surfaceVariant = VisionIntelSurfaceVariantDark,
    onBackground = VisionIntelOnSurfaceDark,
    onSurface = VisionIntelOnSurfaceDark,
    onSurfaceVariant = VisionIntelTextSecondaryDark,
    outline = VisionIntelOutlineVariantDark,
    outlineVariant = VisionIntelOutlineVariantDark,
    error = VisionIntelErrorDark,
    onError = Color(0xFF2C1515),
    errorContainer = Color(0xFF4A1E1E),
    onErrorContainer = VisionIntelOnSurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = VisionIntelBlue,
    onPrimary = Color.White,
    secondary = VisionIntelBlueSoft,
    background = VisionIntelBackgroundLight,
    surface = VisionIntelSurfaceLight,
    surfaceVariant = VisionIntelSurfaceVariantLight,
    onBackground = VisionIntelTextPrimary,
    onSurface = VisionIntelTextPrimary,
    onSurfaceVariant = VisionIntelTextSecondary,
    outline = VisionIntelOutlineVariantLight,
    outlineVariant = VisionIntelOutlineVariantLight,
    error = VisionIntelError,
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = VisionIntelTextPrimary
)

@Composable
fun VisionIntelTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}
