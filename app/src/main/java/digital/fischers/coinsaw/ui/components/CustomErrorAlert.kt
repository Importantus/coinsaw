package digital.fischers.coinsaw.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import digital.fischers.coinsaw.R

@Composable
fun CustomErrorAlert(
    errorCode: Int,
    customTitle: String? = null,
    customMessage: String? = null,
    onConfirm: () -> Unit
) {
    val desc = when (errorCode) {
        400 -> stringResource(id = R.string.error_400)
        401 -> stringResource(id = R.string.error_401)
        403 -> stringResource(id = R.string.error_403)
        404 -> stringResource(id = R.string.error_404)
        500 -> stringResource(id = R.string.error_500)
        else -> stringResource(id = R.string.error_generic)
    }

    AlertDialog(
        onDismissRequest = {},
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.large,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    stringResource(id = R.string.ok),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        title = {
            Text(customTitle ?: stringResource(id = R.string.network_error))
        },
        text = {
            Text(customMessage ?: desc)
        }
    )
}