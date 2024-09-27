package digital.fischers.coinsaw

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import digital.fischers.coinsaw.notifications.NotificationHelper
import digital.fischers.coinsaw.shortcuts.ShortcutHelper
import digital.fischers.coinsaw.ui.CoinsawApp
import digital.fischers.coinsaw.ui.theme.CoinsawTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var shortcutHelper: ShortcutHelper

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoinsawTheme {
                CoinsawApp(intent)
            }
        }

        try {
            notificationHelper.askForNotificationPermission(this)

            shortcutHelper.groupObserver(this)
            notificationHelper.clearGroupNotifications(this)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error setting up notifications", e)
        }
    }
}