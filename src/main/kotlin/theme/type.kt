package theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val customTypography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = FontFamily.Monospace
    ),
    subtitle1 = TextStyle(
        color = greenLight200,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = FontFamily.Monospace
    ),
    caption = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Light,
        fontFamily = FontFamily.Monospace,
        fontStyle = FontStyle.Italic
    ),
    h3 = TextStyle(
        fontSize = 34.sp,
        fontWeight = FontWeight.Black,
        fontFamily = FontFamily.Monospace,
        color = dark
    ),
    h4 = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
    ),
    h5 = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
    ),
    h6 = TextStyle(
        color = Color.Black,
        fontSize = 18.sp,
        fontStyle=FontStyle.Italic,
        fontWeight = FontWeight.Normal,
        fontFamily = FontFamily.Monospace
    )
)