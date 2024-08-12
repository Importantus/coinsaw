package digital.fischers.coinsaw.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import digital.fischers.coinsaw.R

/**
 * Display the token of the share. The token can be copied to the clipboard.
 */
@Composable
fun TokenDisplay(
    token: String
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Text display
        SelectionContainer(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = token,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .clickable(
                    onClick = {
                        val clipboard = getSystemService(context, ClipboardManager::class.java)
                        clipboard?.setPrimaryClip(ClipData.newPlainText("Share Token", token))
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                            Toast
                                .makeText(context, "Key copied", Toast.LENGTH_SHORT)
                                .show()
                    },
                    role = Role.Button
                )
                .background(MaterialTheme.colorScheme.secondary)
                .padding(horizontal = 8.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = R.drawable.icon_copy),
                contentDescription = null
            )

            Text(
                text = stringResource(id = R.string.copy_token),
                modifier = Modifier.padding(end = 8.dp),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            )

        }
    }
}