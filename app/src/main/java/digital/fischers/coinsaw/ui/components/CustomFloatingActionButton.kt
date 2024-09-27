package digital.fischers.coinsaw.ui.components

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import digital.fischers.coinsaw.R

enum class CustomFloatingActionButtonType {
    CONFIRM,
    NEXT,
    ADD
}

@Composable
fun CustomFloatingActionButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    type: CustomFloatingActionButtonType,
    contentDescription: String? = null
) {
    FloatingActionButton(
        onClick = { if (enabled) onClick() },
        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = if(enabled) 1f else 0.5f),
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.imePadding()
    ) {
        when (type) {
            CustomFloatingActionButtonType.CONFIRM -> {
                Icon(
                    painter = painterResource(id = R.drawable.icon_check),
                    contentDescription = contentDescription
                )
            }

            CustomFloatingActionButtonType.NEXT -> {
                Icon(
                    painter = painterResource(id = R.drawable.icon_arrow_big_right),
                    contentDescription = contentDescription
                )
            }

            CustomFloatingActionButtonType.ADD -> {
                Icon(
                    painter = painterResource(id = R.drawable.icon_add),
                    contentDescription = contentDescription
                )
            }
        }

    }

}