package io.github.ronjunevaldoz.bytesweep.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BrandTeal = Color(0xFF0FB5AE)
private val BrandTealDark = Color(0xFF0A8C87)

private val LightColors = lightColorScheme(
    primary = BrandTeal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB7F4F0),
    onPrimaryContainer = Color(0xFF00201E),
    secondary = Color(0xFF4A6360),
)

private val DarkColors = darkColorScheme(
    primary = BrandTeal,
    onPrimary = Color(0xFF00201E),
    primaryContainer = BrandTealDark,
    onPrimaryContainer = Color(0xFFB7F4F0),
    secondary = Color(0xFFB1CCC8),
)

@Composable
fun BytesweepTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
