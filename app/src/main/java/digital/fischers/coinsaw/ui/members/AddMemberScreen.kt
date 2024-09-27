package digital.fischers.coinsaw.ui.members

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.ui.components.BaseScreen
import digital.fischers.coinsaw.ui.components.CustomFloatingActionButton
import digital.fischers.coinsaw.ui.components.CustomFloatingActionButtonType
import digital.fischers.coinsaw.ui.components.CustomNavigationBar
import digital.fischers.coinsaw.ui.viewModels.AddMemberViewModel
import kotlinx.coroutines.launch

@Composable
fun AddMemberScreen(
    onBackNavigation: (String) -> Unit,
    onForwardNavigation: (String) -> Unit,
    isInGroupCreationFlow: Boolean = false,
    viewModel: AddMemberViewModel = hiltViewModel()
) {
    val groupId = viewModel.groupId
    val loading = viewModel.loading
    val newUserState by viewModel.newUserState.collectAsState()
    val valid = viewModel.valid

    val coroutineScope = rememberCoroutineScope()

    BaseScreen(
        appBar = {
            CustomNavigationBar(
                title = stringResource(id = R.string.add_member),
                backNavigationText = null,
                backNavigation = { onBackNavigation(groupId) }
            )
        },
        floatingActionButton = {
            CustomFloatingActionButton(
                enabled = valid,
                onClick = {
                    coroutineScope.launch {
                        viewModel.createUser()
                        onForwardNavigation(groupId)
                    }
                },
                type = if (isInGroupCreationFlow) CustomFloatingActionButtonType.NEXT
                else CustomFloatingActionButtonType.CONFIRM
            )
        },
        blockingLoading = loading,
        title = stringResource(id = R.string.add_member)
    ) {
        EditMemberForm(
            onNameChange = { viewModel.onNameChanged(it) },
            onIsMeChange = { viewModel.onIsMeChanged(it) },
            name = newUserState.name,
            isMe = newUserState.isMe
        )
    }
}