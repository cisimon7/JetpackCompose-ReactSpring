package theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = green200,
    primaryVariant = green700,
    secondary = greenLight200
)

private val LightColorPalette = lightColors(
    primary = green200,
    primaryVariant = green700,
    secondary = greenLight200,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = dark,
    onBackground = dark,
    onSurface = dark
)

@Composable
@Suppress("FunctionName")
fun MyTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = customTypography,
        shapes = shapes,
        content = content
    )
}