package com.example.proyectodejuegowifi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ColorBackground = Color(0xFFF5F5F0)
val ColorSurface = Color(0xFFFFFFFF)
val ColorPrimary = Color(0xFF1A1A1A)
val ColorSecondary = Color(0xFF3D3D3D)
val ColorAccent = Color(0xFF1A1A1A)
val ColorDanger = Color(0xFFE53935)
val ColorBorder = Color(0xFFE0E0E0)
val ColorMuted = Color(0xFF9E9E9E)

private val GameColorScheme = lightColorScheme(
    primary = ColorPrimary,
    secondary = ColorSecondary,
    background = ColorBackground,
    surface = ColorSurface,
    onPrimary = Color.White,
    onBackground = ColorPrimary,
    onSurface = ColorPrimary,
)

@Composable
fun BombGameTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GameColorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
