package com.synapse.social.studioasinc.web.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val darkColors = darkColorScheme(
    background = Color(0xFF0E0E10),
    surface = Color(0xFF0E0E10),
    surfaceContainer = Color(0xFF19191D),
    surfaceContainerHigh = Color(0xFF1F1F24),
    primary = Color(0xFFC6C6C7),
    onSurface = Color(0xFFE7E4EC),
    onSurfaceVariant = Color(0xFFACAAB1),
    secondary = Color(0xFF9F9DA1),
    outline = Color(0xFF75757C),
    error = Color(0xFFEC7C8A)
)

@Composable
fun WebTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColors,
        content = content
    )
}
