package digital.fischers.coinsaw.ui.group

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.ui.components.BaseScreen
import digital.fischers.coinsaw.ui.components.CustomFloatingActionButton
import digital.fischers.coinsaw.ui.components.CustomFloatingActionButtonType
import digital.fischers.coinsaw.ui.components.CustomNavigationBar
import digital.fischers.coinsaw.ui.theme.neutral
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
            CustomFloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        val groupId = viewModel.createGroup()
                        onNavigateForward(groupId)
                    }
                },
                type = CustomFloatingActionButtonType.NEXT,
                contentDescription = stringResource(id = R.string.next_step)
            )
        }
    ) {
        Text(
            text = stringResource(id = R.string.screen_newgroup_title),
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.neutral
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        GroupEditForm(
            groupName = groupName,
            currency = currency,
            onNameChange = viewModel::onGroupNameChanged,
            onCurrencyChange = viewModel::onCurrencyChanged
        )
    }
}