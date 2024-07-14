package digital.fischers.coinsaw.ui.utils

import java.math.RoundingMode

fun Double.roundHalfUp(decimalPlaces: Int = 2): Double {
    return this.toBigDecimal().setScale(decimalPlaces, RoundingMode.HALF_UP).toDouble()
}