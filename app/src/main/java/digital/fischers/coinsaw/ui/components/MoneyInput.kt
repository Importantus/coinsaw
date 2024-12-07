package digital.fischers.coinsaw.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import digital.fischers.coinsaw.ui.utils.formatAsDecimal
import java.util.Locale

@Composable
fun MoneyInput(
    onValueChanged: (Double) -> Unit,
    value: Double,
    currency: String = "€"
) {
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember {
        MutableInteractionSource()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = getBottomLineShape(2.dp)
            )
            .clip(MaterialTheme.shapes.medium)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) {
                focusRequester.requestFocus()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            BasicTextField(
                value = moneyToString(value),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Light,
                    fontSize = 48.sp
                ),
                onValueChange = { onValueChanged(stringToMoney(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .background(Color.Transparent)
                    .padding(8.dp)
            )
            if (value == 0.0) {
                Text(
                    text = "0.00", modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Light,
                        fontSize = 48.sp,
                    ),
                    overflow = TextOverflow.Clip,
                    maxLines = 1
                )
            }
        }
        Text(
            modifier = Modifier
                .padding(end = 8.dp),
            text = currency,
            style = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Light,
                fontSize = 40.sp
            )
        )
    }
}

fun moneyToString(value: Double): String {
    return if (value == 0.0) {
        ""
    } else {
        String.format(Locale.getDefault(), "%.2f", value)
    }
}

fun stringToMoney(value: String): Double {
    return value.formatAsDecimal().toDoubleOrNull() ?: 0.0
}