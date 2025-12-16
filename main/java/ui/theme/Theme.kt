package com.example.penjadwalan_sidang.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4A90E2),
    secondary = Color(0xFF8B5CF6),
    tertiary = Color(0xFFFFF5F5),
    background = Color(0xFFFFF5F5),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

@Composable
fun PengajuanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Karena Anda tidak menggunakan darkTheme, ColorScheme tetap LightColorScheme
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(), // Pastikan Typography() sudah Anda definisikan
        content = content
    )
}