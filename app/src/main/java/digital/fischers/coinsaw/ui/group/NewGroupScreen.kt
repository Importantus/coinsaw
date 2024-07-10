package digital.fischers.coinsaw.ui.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.material.textfield.TextInputLayout
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.ui.components.BaseScreen
import digital.fischers.coinsaw.ui.components.CustomNavigationBar
import digital.fischers.coinsaw.ui.components.CustomTextField
import digital.fischers.coinsaw.ui.components.SettingCard
import digital.fischers.coinsaw.ui.viewModels.NewGroupViewModel
import kotlinx.coroutines.launch

@Composable
fun NewGroupScreen(
    onNavigateBack: () -> Unit,
    onNavigateForward: (String) -> Unit,
    viewModel: NewGroupViewModel = hiltViewModel()
) {
    val groupName = viewModel.groupName
    val currency = viewModel.currency
    val loading = viewModel.loading

    val coroutineScope = rememberCoroutineScope()

    BaseScreen(
        blockingLoading = loading,
        appBar = {
            CustomNavigationBar(
                title = stringResource(id = R.string.add_group),
                backNavigationText = stringResource(id = R.string.cancel),
                backNavigation = { onNavigateBack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        val groupId = viewModel.createGroup()
                        onNavigateForward(groupId)
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_arrow_big_right),
                    contentDescription = stringResource(
                        id = R.string.next_step
                    )
                )
            }
        }
    ) {
        Text(
            text = stringResource(id = R.string.screen_newgroup_title),
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomTextField(
            value = groupName,
            onValueChange = { viewModel.onGroupNameChanged(it) },
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
                    onValueChange = { viewModel.onCurrencyChanged(it) },
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
}