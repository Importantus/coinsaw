package digital.fischers.coinsaw.ui.utils

import android.content.Context
import android.icu.text.DateFormat
import android.icu.util.Calendar
import digital.fischers.coinsaw.R
import java.util.Locale

fun getDate(timestamp: Long): String? {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp
    return calendar.getDateTimeFormat(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault())
        .format(calendar.time)
}

fun getTime(timestamp: Long): String? {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp
    return calendar.getDateTimeFormat(DateFormat.NONE, DateFormat.SHORT, Locale.getDefault())
        .format(calendar.time)
}

/**
 * If the timestamp is less than 1 hour ago, return the minutes difference.
 * If the timestamp is on the same day, return the time (eg. 12:00 PM).
 * If the timestamp is yesterday, return "Yesterday" and the time.
 * Else return the full date.
 */
fun getTimeDifference(timestamp: Long, context: Context): String {
    val calendar = Calendar.getInstance()
    val currentTime = calendar.timeInMillis
    val difference = currentTime - timestamp
    val minutes = difference / 60000
    val hours = minutes / 60

    val yesterday = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_MONTH, -1)
    }

    return when {
        minutes < 1 -> context.getString(R.string.just_now)
        minutes < 60 -> context.getString(R.string.minutes_ago, minutes)
        timestamp > yesterday.timeInMillis -> context.getString(R.string.time_format, getTime(timestamp))
        timestamp > calendar.apply { add(Calendar.DAY_OF_MONTH, -2) }.timeInMillis -> context.getString(R.string.yesterday_time_format, getTime(timestamp))
        else -> context.getString(R.string.date_format, getDate(timestamp))
    }
}