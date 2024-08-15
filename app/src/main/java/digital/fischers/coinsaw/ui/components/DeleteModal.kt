package digital.fischers.coinsaw.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import digital.fischers.coinsaw.R

@Composable
fun DeleteModal(
    title: String,
    description: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.large,
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    color = MaterialTheme.colorScheme.error,
                    text = stringResource(id = R.string.delete)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(
                    color = MaterialTheme.colorScheme.onBackground,
                    text = stringResource(id = R.string.cancel)
                )
            }
        },
        title = {
            Text(title)
        },
        text = {
            Text(description)
        }
    )
}