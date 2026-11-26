package shaketaunt.mobile.mypapit.net.shaketaunt.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val IronPrimary = Color(0xFF424242)
val IronPrimaryDark = Color(0xFF1B1B1B)
val IronAccent = Color(0xFF6D6D6D)
val IronSurface = Color(0xFF1F1F1F)
val IronSurfaceVariant = Color(0xFF2C2C2C)
val IronOutline = Color(0xFF555555)
val PureWhite = Color(0xFFFFFFFF)
val SoftSilver = Color(0xFFBDBDBD)

private val ShakeTaunterDarkColorScheme = darkColorScheme(
    primary = IronPrimary,
    onPrimary = PureWhite,
    primaryContainer = IronSurfaceVariant,
    onPrimaryContainer = PureWhite,
    secondary = IronAccent,
    onSecondary = PureWhite,
    secondaryContainer = IronSurfaceVariant,
    onSecondaryContainer = SoftSilver,
    background = IronPrimaryDark,
    onBackground = PureWhite,
    surface = IronSurface,
    onSurface = PureWhite,
    surfaceVariant = IronSurfaceVariant,
    onSurfaceVariant = SoftSilver,
    outline = IronOutline,
)

private val ShakeTaunterTypography = Typography(
    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 38.sp,
        lineHeight = 46.sp,
        letterSpacing = 0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
)

@Composable
fun ShakeTaunterTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ShakeTaunterDarkColorScheme,
        typography = ShakeTaunterTypography,
        content = content,
    )
}
