package digital.fischers.coinsaw.ui.members

import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.ui.components.BaseScreen
import digital.fischers.coinsaw.ui.components.CustomButton
import digital.fischers.coinsaw.ui.components.CustomFloatingActionButton
import digital.fischers.coinsaw.ui.components.CustomFloatingActionButtonType
import digital.fischers.coinsaw.ui.components.CustomNavigationBar
import digital.fischers.coinsaw.ui.components.DeleteModal
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
    val valid = viewModel.valid

    val coroutineScope = rememberCoroutineScope()

    var menuExpanded by remember { mutableStateOf(false) }
    var showDeleteModal by remember { mutableStateOf(false) }

    BaseScreen(
        appBar = {
            CustomNavigationBar(
                title = stringResource(id = R.string.screen_edit_member_title),
                backNavigationText = null,
                backNavigation = { onBackNavigation(groupId) },
                menu = {
                    CustomButton(
                        onClick = { menuExpanded = true },
                        icon = R.drawable.icon_dot_menu
                    )
                    DropdownMenu(
                        offset = DpOffset(0.dp, 5.dp),
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface),
                    ) {
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_delete),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            },
                            text = {
                                Text(text = stringResource(id = R.string.delete_member_title))
                            },
                            onClick = {
                                showDeleteModal = true
                                menuExpanded = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            CustomFloatingActionButton(
                enabled = valid,
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

        if (showDeleteModal) {
            DeleteModal(
                title = stringResource(id = R.string.delete_member_title),
                description = stringResource(id = R.string.delete_member_description, name),
                onConfirm = {
                    coroutineScope.launch {
                        viewModel.deleteUser()
                    }.invokeOnCompletion {
                        showDeleteModal = false
                        onBackNavigation(groupId)
                    }
                },
                onCancel = { showDeleteModal = false }
            )
        }
    }
}