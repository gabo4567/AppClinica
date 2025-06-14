package com.clinica.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AzulClinica = Color(0xFF1976D2) // Azul intermedio, no tan claro

private val LightColorScheme = lightColorScheme(
    primary = AzulClinica,
    onPrimary = Color.White,
    secondary = AzulClinica,
    onSecondary = Color.White,
    background = Color(0xFFF7F7F7),
    surface = Color.White,
    onSurface = Color.Black,
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(),
        content = content
    )
}