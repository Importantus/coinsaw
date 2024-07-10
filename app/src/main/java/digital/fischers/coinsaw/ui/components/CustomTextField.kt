package digital.fischers.coinsaw.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    OutlinedTextField(
        label = {  Text(label) },
        isError = isError,
        singleLine = true,
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            errorContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.0f),
            errorIndicatorColor = MaterialTheme.colorScheme.error,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onBackground,
            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground
        ),
        modifier = modifier
            .fillMaxWidth()
    )
}