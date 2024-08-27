package digital.fischers.coinsaw.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

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

    val view = LocalView.current
    val window = (view.context as Activity).window

    SideEffect {
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }

    MaterialTheme(
        shapes = MaterialTheme.shapes.copy(
            extraSmall = RoundedCornerShape(10.dp)
        ),
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}