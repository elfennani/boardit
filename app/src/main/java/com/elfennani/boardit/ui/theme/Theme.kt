package com.elfennani.boardit.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF27272a),
    onPrimary = Color.White,
    secondaryContainer = Color(0xFFdbeafe),
    secondary = Color(0xFF3b82f6),
    onSecondaryContainer = Color(0xFF3b82f6),
    background = Color.Black,
    surface = Color.Black,
    onSurface = Color.White,
    onBackground = Color.White,
    error = Color(0xFFef4444),
    outline = Color(0xFF3f3f46),
    tertiary = Color(0xFFd4d4d8)
)

private val LightColorScheme = lightColorScheme(
    primary = Color.Black,
    secondaryContainer = Color(0xFFdbeafe),
    secondary = Color(0xFF3b82f6),
    onSecondaryContainer = Color(0xFF3b82f6),
    background = Color.White,
    surface = Color(0xFFF2F2F7),
    onSurface = Color(0xFF0f172a),
    onBackground = Color(0xFF0f172a),
    error = Color(0xFFef4444),
    outline = Color(0xFFd1d5db),
    tertiary = Color(0xFF6b7280)
)

@Composable
fun BoarditTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}