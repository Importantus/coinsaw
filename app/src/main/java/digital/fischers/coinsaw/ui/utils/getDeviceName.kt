package digital.fischers.coinsaw.ui.utils

import android.content.ContentResolver
import android.provider.Settings

fun getDeviceName(contentResolver: ContentResolver): String {
    // List of functions to get device name
    val functions = listOf(
        { Settings.Secure.getString(contentResolver, "bluetooth_name") },
        { Settings.System.getString(contentResolver, "bluetooth_name") },
        { Settings.System.getString(contentResolver, "device_name") },
        { android.os.Build.MODEL }
    )

    // Return the first non-null value
    return functions.firstNotNullOfOrNull {
        try {
            it()
        } catch (e: Exception) {
            null
        }
    } ?: "Unknown Device"
}