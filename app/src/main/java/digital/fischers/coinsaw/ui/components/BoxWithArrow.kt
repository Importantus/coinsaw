package digital.fischers.coinsaw.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp

@Composable
fun BoxWithArrowRight(
    color: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .widthIn(min = 100.dp, max = 200.dp),

        ) {
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {

            drawBoxWithArrow(color = color)
        }
        Box(
            modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
        ) {
            content()
        }
    }
}

fun DrawScope.drawBoxWithArrow(color: Color) {
    val path = Path().apply {
        moveTo(0f, 10f)
        arcTo(
            rect = Rect(0f, 0f, 20f, 20f),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        lineTo(size.width - 30f, 0f)
        arcTo(
            rect = Rect(size.width - 40f, 0f, size.width - 20f, 20f),
            startAngleDegrees = 270f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        lineTo(size.width - 20f, size.height / 2 - 20f)
        lineTo(size.width, size.height / 2)
        lineTo(size.width - 20f, size.height / 2 + 20f)
        lineTo(size.width - 20f, size.height - 10f)
        arcTo(
            rect = Rect(size.width - 40f, size.height - 20f, size.width - 20f, size.height),
            startAngleDegrees = 0f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        lineTo(20f, size.height)
        arcTo(
            rect = Rect(0f, size.height - 20f, 20f, size.height),
            startAngleDegrees = 90f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        close()
    }
    drawPath(path, color = color)
}