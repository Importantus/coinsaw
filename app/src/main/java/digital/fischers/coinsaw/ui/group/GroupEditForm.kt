package digital.fischers.coinsaw.ui.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.ui.components.CustomTextField
import digital.fischers.coinsaw.ui.components.SettingCard

@Composable
fun GroupEditForm(
    groupName: String,
    currency: String,
    onNameChange: (String) -> Unit,
    onCurrencyChange: (String) -> Unit
) {
    CustomTextField(
        value = groupName,
        onValueChange = { onNameChange(nameValidator(it)) },
        label = stringResource(id = R.string.groupname),
        placeholder = stringResource(id = R.string.groupname_placeholder)
    )

    Spacer(modifier = Modifier.height(16.dp))

    SettingCard(
        title = stringResource(id = R.string.currency),
        description = stringResource(id = R.string.currency_description),
        setting = {
            BasicTextField(
                value = currency,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                onValueChange = { onCurrencyChange(currencyValidator(it)) },
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                ),
                singleLine = true,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(8.dp)
                    .width(40.dp)
            )
        })
}

fun currencyValidator(currency: String): String {
    return currency.take(3)
}

fun nameValidator(name: String): String {
    return name.take(75)
}