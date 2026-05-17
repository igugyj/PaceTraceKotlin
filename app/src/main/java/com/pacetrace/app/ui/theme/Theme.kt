package com.pacetrace.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),
    onPrimary = Color.White,
    secondary = Color(0xFF2E7D32),
    onSecondary = Color.White,
    surface = Color.White,
    onSurface = Color(0xFF1C1C1C),
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF1C1C1C),
    surfaceVariant = Color(0xFFF0F0F0),
    outline = Color(0xFFDDDDDD)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF42A5F5),
    onPrimary = Color.Black,
    secondary = Color(0xFF66BB6A),
    onSecondary = Color.Black,
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2C2C2C),
    outline = Color(0xFF444444)
)

@Composable
fun PaceTraceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}
