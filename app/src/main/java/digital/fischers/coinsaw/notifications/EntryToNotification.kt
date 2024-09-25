package digital.fischers.coinsaw.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import digital.fischers.coinsaw.MainActivity
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.domain.changelog.Entry
import digital.fischers.coinsaw.domain.changelog.EntryAction
import digital.fischers.coinsaw.domain.changelog.EntryType
import digital.fischers.coinsaw.domain.changelog.Payload
import digital.fischers.coinsaw.domain.repository.GroupRepository
import digital.fischers.coinsaw.domain.repository.UserRepository
import digital.fischers.coinsaw.ui.Screen
import kotlinx.coroutines.flow.firstOrNull

suspend fun Entry.toNotification(
    context: Context,
    groupRepository: GroupRepository,
    userRepository: UserRepository
): Notification? {
    val group = groupRepository.getGroupStream(groupId).firstOrNull() ?: return null

    if(group.open == true) return null

    val (title, message) = when (type) {
        EntryType.SETTINGS -> {
            if (action == EntryAction.CREATE) return null

            val payload = payload as Payload.GroupSettings
            // For now, we only care about currency changes
            if (payload.currency.isNullOrEmpty()) return null

            val currency = payload.currency
            val name = payload.name ?: group.name

            Pair(
                context.getString(R.string.notification_group_settings_title, name),
                context.getString(R.string.notification_group_settings_message, currency)
            )
        }

        EntryType.USER -> {
            if (action == EntryAction.UPDATE) return null

            val payload = payload as Payload.User
            val name = payload.name ?: context.getString(R.string.unknown_user)

            Pair(
                context.getString(R.string.notification_new_user_title, group.name),
                context.getString(R.string.notification_new_user_message, name, group.name)
            )
        }

        EntryType.BILL -> {
            if (action == EntryAction.UPDATE) return null

            val payload = payload as Payload.Bill
            if (payload.payerId == null || payload.participants?.get(0)?.userId == null) return null

            val name = payload.name

            val payer = userRepository.getUserStream(payload.payerId).firstOrNull() ?: return null

            val amount = "%.2f".format(payload.amount) + " ${group.currency}"

            if (name.isNullOrBlank() && payload.participants.size == 1) {
                val payee =
                    userRepository.getUserStream(payload.participants[0].userId).firstOrNull()
                        ?: return null

                Pair(
                    context.getString(R.string.notification_new_transaction_title, group.name),
                    context.getString(
                        R.string.notification_new_transaction_message,
                        payer.name,
                        amount,
                        payee.name
                    )
                )
            } else {
                Pair(
                    context.getString(R.string.notification_new_bill_title, group.name),
                    context.getString(
                        R.string.notification_new_bill_message,
                        payer.name,
                        amount,
                        name
                    )
                )
            }
        }
    }

    val intent = Intent(context, MainActivity::class.java)
        .setAction(Intent.ACTION_VIEW)
        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).apply {
            data = Uri.parse(Screen.Group.createDeepLink(groupId))
        }

    return NotificationCompat.Builder(context, NotificationHelper.CHANNEL_CHANGELOG_ID)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setContentIntent(
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        .build()
}