package digital.fischers.coinsaw.shortcuts

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import digital.fischers.coinsaw.MainActivity
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.data.database.Group
import digital.fischers.coinsaw.domain.repository.GroupRepository
import digital.fischers.coinsaw.ui.Screen

class ShortcutHelper(
    private val context: Context,
    private val groupRepository: GroupRepository
) {
    private var listeningToGroupChanges = false

    fun groupObserver(lifecycleOwner: LifecycleOwner) {
        if (listeningToGroupChanges) return
        groupRepository.getAllGroupsStream().asLiveData().observe(lifecycleOwner) { groups ->
            groups.sortedBy { it.lastOpenedAt }.forEach { group ->
                Log.d("ShortcutHelper", "Pushing shortcut for group ${group.name}")
                pushAddBillShortcut(group.id, group.name)
            }
            // Remove shortcuts for groups that are not in the list
            ShortcutManagerCompat.getDynamicShortcuts(context).forEach { shortcut ->
                if (groups.none { it.id == shortcut.id }) {
                    disableShortcut(shortcut.id)
                }
            }
        }
        listeningToGroupChanges = true
    }

    private fun pushAddBillShortcut(groupId: String, groupName: String) {
        if(groupName.isEmpty()) return

        val shortcut = ShortcutInfoCompat.Builder(context, groupId)
            .setShortLabel(groupName)
            .setLongLabel(context.resources.getString(R.string.add_bill_shortcut_desc) + " " + groupName)
            .setIcon(
                IconCompat.createWithResource(
                    context, R.drawable.icon_add
                )
            )
            .setIntent(
                Intent(context, MainActivity::class.java)
                    .setAction(Intent.ACTION_VIEW)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).apply {
                        data = Uri.parse(Screen.AddBill.createDeepLink(groupId))
                    }
            )
            .build()

        ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
    }

    private fun disableShortcut(groupId: String) {
        ShortcutManagerCompat.disableShortcuts(
            context, listOf(groupId), context.resources.getString(
                R.string.add_bill_shortcut_disabled
            )
        )
        ShortcutManagerCompat.removeDynamicShortcuts(context, listOf(groupId))
    }
}