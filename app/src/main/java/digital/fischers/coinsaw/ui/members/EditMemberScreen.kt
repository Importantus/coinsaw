package digital.fischers.coinsaw.ui.members

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.ui.components.BaseScreen
import digital.fischers.coinsaw.ui.components.CustomFloatingActionButton
import digital.fischers.coinsaw.ui.components.CustomFloatingActionButtonType
import digital.fischers.coinsaw.ui.components.CustomNavigationBar
import digital.fischers.coinsaw.ui.viewModels.EditMemberViewModel
import kotlinx.coroutines.launch

@Composable
fun EditMemberScreen(
    onBackNavigation: (String) -> Unit,
    isInGroupCreationFlow: Boolean = false,
    viewModel: EditMemberViewModel = hiltViewModel()
) {
    val groupId = viewModel.groupId
    val loading = viewModel.loading
    val name = viewModel.userName
    val isMe = viewModel.isMe

    val coroutineScope = rememberCoroutineScope()

    BaseScreen(
        appBar = {
            CustomNavigationBar(
                title = stringResource(id = R.string.screen_edit_member_title),
                backNavigationText = null,
                backNavigation = { onBackNavigation(groupId) }
            )
        },
        floatingActionButton = {
            CustomFloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        viewModel.saveChanges()
                        onBackNavigation(groupId)
                    }
                },
                type = CustomFloatingActionButtonType.CONFIRM
            )
        },
        blockingLoading = loading,
    ) {
        EditMemberForm(
            onNameChange = { viewModel.onNameChanged(it) },
            onIsMeChange = { viewModel.onIsMeChanged(it) },
            name = name,
            isMe = isMe
        )
    }
}