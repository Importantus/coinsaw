package digital.fischers.coinsaw.ui.components

import android.graphics.drawable.shapes.Shape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun getBottomLineShape(lineThicknessDp: Dp) : GenericShape {
    val lineThicknessPx = with(LocalDensity.current) {lineThicknessDp.toPx()}
    return GenericShape { size, _ ->
        // 1) Bottom-left corner
        moveTo(0f, size.height)
        // 2) Bottom-right corner
        lineTo(size.width, size.height)
        // 3) Top-right corner
        lineTo(size.width, size.height - lineThicknessPx)
        // 4) Top-left corner
        lineTo(0f, size.height - lineThicknessPx)
    }
}