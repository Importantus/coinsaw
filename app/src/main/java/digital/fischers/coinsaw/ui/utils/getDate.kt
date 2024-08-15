package digital.fischers.coinsaw.ui.utils

import android.icu.text.DateFormat
import android.icu.util.Calendar
import java.util.Locale

fun getDate(timestamp: Long): String? {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp
    return calendar.getDateTimeFormat(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault())
        .format(calendar.time)
}