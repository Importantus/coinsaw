package digital.fischers.coinsaw.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.data.database.Changelog
import digital.fischers.coinsaw.domain.changelog.Entry
import digital.fischers.coinsaw.domain.repository.ChangelogRepository
import digital.fischers.coinsaw.domain.repository.GroupRepository
import digital.fischers.coinsaw.domain.repository.UserRepository

class NotificationHelper(
    private val context: Context,
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val changelogRepository: ChangelogRepository
) {
    companion object NotificationChannels {
        const val CHANNEL_CHANGELOG_ID = "coinsaw.changelog"
    }

    private val groupObservers = mutableMapOf<String, LiveData<List<Changelog>>>()
    private val notificationManager: NotificationManager =
        context.getSystemService(NotificationManager::class.java)

    fun setUpNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Set up notification channels
            if (notificationManager.getNotificationChannel(CHANNEL_CHANGELOG_ID) == null) {
                notificationManager.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_CHANGELOG_ID,
                        context.getString(R.string.channel_changelog),
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        description = context.getString(R.string.channel_changelog_description)
                    }
                )
            }
        }
    }

    private fun dismissNotification(id: Int) {
        notificationManager.cancel(id)
    }

    suspend fun showChangelogNotification(entry: Entry) {
        // Show a notification for the changelog entry
        val notification = entry.toNotification(context, groupRepository, userRepository) ?: return
        Log.d("NotificationHelper", "Showing notification for entry ${entry.id.hashCode()}")
        notificationManager.notify(entry.id.hashCode(), notification)
    }

    fun clearGroupNotifications(lifecycleOwner: LifecycleOwner) {
        groupRepository.getAllGroupsStream().asLiveData().observe(lifecycleOwner) { groups ->
            groups.forEach { group ->
                Log.d("NotificationHelper", "Checking group ${group.name} ${group.open}")
                if (group.open == true) {
                    val liveData =
                        changelogRepository.getChangelogByGroupAddedLocallyAfterTimestampSynced(
                            group.id,
                            (group.lastSync?.minus(10000)) ?: 0
                        )
                            .asLiveData()

                    liveData.observe(lifecycleOwner) { changelog ->
                        changelog.forEach { entry ->
                            Log.d("NotificationHelper", "Dismissing notification for entry ${entry.id.hashCode()}")
                            dismissNotification(entry.id.hashCode())
                        }
                    }

                    groupObservers[group.id] = liveData
                } else {
                    // Cancel observer
                    Log.d("NotificationHelper", "Removing observer for group ${group.id}")
                    groupObservers[group.id]?.removeObservers(lifecycleOwner)
                }
            }

        }
    }

}