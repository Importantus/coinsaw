package digital.fischers.coinsaw.intents

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import digital.fischers.coinsaw.MainActivity
import digital.fischers.coinsaw.R

fun pushAddBillShortcut(context: Context, groupId: String, groupName: String) {
    val shortcut = ShortcutInfoCompat.Builder(context, groupId)
        .setShortLabel(groupName)
        .setLongLabel(context.resources.getString(R.string.add_bill_shortcut_desc) + " " + groupName)
        .setIcon(IconCompat.createWithResource(
            context, R.drawable.icon_add
        ))
        .setIntent(
            Intent(context, MainActivity::class.java)
                .setAction(Intent.ACTION_VIEW)
                .putExtra("groupId", groupId)
        )
        .build()

    ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
}

fun disableAddBillShortcut(context: Context, groupId: String) {
    ShortcutManagerCompat.disableShortcuts(context, listOf(groupId), context.resources.getString(R.string.add_bill_shortcut_disabled))
    ShortcutManagerCompat.removeDynamicShortcuts(context, listOf(groupId))
}