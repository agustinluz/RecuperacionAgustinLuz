package com.recuperacion.agustin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2196F3),        // Azul principal
    onPrimary = Color.White,
    secondary = Color(0xFF64B5F6),      // Azul más claro
    onSecondary = Color.White,
    tertiary = Color(0xFF1976D2),       // Azul más oscuro
    onTertiary = Color.White,
    background = Color.White,
    onBackground = Color(0xFF1976D2),
    surface = Color.White,
    onSurface = Color(0xFF1976D2),
    error = Color(0xFFB00020)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF2196F3),
    onPrimary = Color.White,
    secondary = Color(0xFF64B5F6),
    onSecondary = Color.White,
    tertiary = Color(0xFF1976D2),
    onTertiary = Color.White,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF121212),
    onSurface = Color.White,
    error = Color(0xFFCF6679)
)
//

@Composable
fun  RecuperacionAgustinLuzTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}