package digital.fischers.coinsaw.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = sunnyYellow,
    secondary = deepBlue,
    tertiary = oceanTurquoise,
    surface = midnightBlack,
    surfaceVariant = navyBlue,
    background = darkNavy,
    error = fieryRed,
    secondaryContainer = leafGreen,
    onBackground = mistyGrey
)

@Composable
fun CoinsawTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
    darkTheme: Boolean = true,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        shapes = MaterialTheme.shapes.copy(
            extraSmall = RoundedCornerShape(10.dp)
        ),
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}