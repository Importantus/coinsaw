package digital.fischers.coinsaw.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xffddfe65),
    secondary = Color(0xff014751),
    tertiary = Color(0xff02b7d6),
    surface = Color(0xff00242c),
    surfaceVariant = Color(0xff0f2f33),
    background = Color(0xff001519),
    error = Color(0xfffd7374),
    secondaryContainer = Color(0xff68c17d),
    onBackground = Color(0xff8bafb5),
    onPrimary = Color(0xFF56575A)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFD7FD49),
    secondary = Color(0xFF97A9B1),
    tertiary = Color(0xFFA6D8D3),
    surface = Color(0xFFBACACF),
    surfaceVariant = Color(0xFFA5B4BB),
    background = Color(0xFFD1DADA),
    error = Color(0xfffd7374),
    secondaryContainer = Color(0xFF36AD53),
    onBackground = Color(0xFF3D3F41),
    onPrimary = Color(0xFF56575A)
)

val ColorScheme.neutral: Color @Composable get() = if (!isSystemInDarkTheme()) Color(0xFF333333) else Color(0xFFf5f5f5)

@Composable
fun CoinsawTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
//    darkTheme: Boolean = true,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

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