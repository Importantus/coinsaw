package digital.fischers.coinsaw.ui.members

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.ui.components.CustomTextField

@Composable
fun EditMemberForm(
    onNameChange: (String) -> Unit,
    onIsMeChange: (Boolean) -> Unit,
    name: String,
    isMe: Boolean
) {
    CustomTextField(
        value = name,
        onValueChange = { onNameChange(nameValidator(it)) },
        placeholder = stringResource(R.string.member_name_placeholder),
        label = stringResource(R.string.member_name),
    )
    Row(
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth()
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.member_is_user_form_label),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
            )
        )
        Switch(
            checked = isMe, onCheckedChange = { onIsMeChange(it) },
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                uncheckedTrackColor = MaterialTheme.colorScheme.surface,
                uncheckedBorderColor = MaterialTheme.colorScheme.background,
                checkedBorderColor = MaterialTheme.colorScheme.background,
                checkedThumbColor = MaterialTheme.colorScheme.surface,
            )
        )
    }
}

fun nameValidator(name: String): String {
    return name.take(75)
}