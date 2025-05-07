package com.example.chaintechinterviewtask.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF4E8EDA),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB8E8B9),
    onPrimaryContainer = Color(0xFF002106),
    secondary = Color(0xFF4A6267),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCE8ED),
    onSecondaryContainer = Color(0xFF051F23),
    tertiary = Color(0xFF4A607C),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD1E4FF),
    onTertiaryContainer = Color(0xFF041C35),
    error = Color(0xFFBA1A1A),
    background = Color(0xFFF8FDFF),
    onBackground = Color(0xFF001F25),
    surface = Color(0xFFF8FDFF),
    onSurface = Color(0xFF001F25)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF4E8EDA),
    onPrimary = Color(0xFF00390F),
    primaryContainer = Color(0xFF005317),
    onPrimaryContainer = Color(0xFFB8E8B9),
    secondary = Color(0xFFB1CCD1),
    onSecondary = Color(0xFF1C3438),
    secondaryContainer = Color(0xFF334B4F),
    onSecondaryContainer = Color(0xFFCCE8ED),
    tertiary = Color(0xFFB1C5E8),
    onTertiary = Color(0xFF1D324B),
    tertiaryContainer = Color(0xFF334863),
    onTertiaryContainer = Color(0xFFD1E4FF),
    error = Color(0xFFFFB4AB),
    background = Color(0xFF001F25),
    onBackground = Color(0xFFA6EEFF),
    surface = Color(0xFF001F25),
    onSurface = Color(0xFFA6EEFF)
)

@Composable
fun SecurePassTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}