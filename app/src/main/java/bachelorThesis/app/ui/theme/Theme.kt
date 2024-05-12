package bachelorThesis.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette =
    darkColorScheme(
        primary = LightBlue1000,
        secondary = LightGray,
        background = DarkGray,
        surface = MediumGray,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = LightBlue1000,
        onSurface = LightBlue1000
)

private val LightColorPalette by lazy {
    lightColorScheme(
        primary = LightBlue1000,
        secondary = LightBlueComplementary,
        background = Color.White,
        surface = LightGray,
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onBackground = DarkGray,
        onSurface = DarkGray

        /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
    )
}

@Composable
fun AppTheme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}