package digital.fischers.coinsaw.ui.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import digital.fischers.coinsaw.R

enum class CustomFloatingActionButtonType {
    CONFIRM,
    NEXT,
    ADD
}

@Composable
fun CustomFloatingActionButton(
    onClick: () -> Unit,
    type: CustomFloatingActionButtonType,
    contentDescription: String? = null
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.secondary
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