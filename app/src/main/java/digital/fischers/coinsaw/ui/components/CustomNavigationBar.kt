package digital.fischers.coinsaw.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import digital.fischers.coinsaw.R

@Composable
fun CustomNavigationBar(
    title: String,
    backNavigationText: String?,
    backNavigation: () -> Unit,
    menu: @Composable (() -> Unit)? = null
) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CustomButton(
                    icon = R.drawable.icon_arrow_left,
                    text = backNavigationText,
                    onClick = { backNavigation() })

                Box(modifier = Modifier
                    .wrapContentSize(Alignment.TopEnd)) {
                    menu?.invoke()
                }
            }
            Text(
                text = title,
                modifier = Modifier.align(Alignment.Center),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

        }


}