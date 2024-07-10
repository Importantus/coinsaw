package digital.fischers.coinsaw.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

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
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}