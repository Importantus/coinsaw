package digital.fischers.coinsaw.ui.group

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.intents.disableAddBillShortcut
import digital.fischers.coinsaw.ui.components.BaseScreen
import digital.fischers.coinsaw.ui.components.CustomFloatingActionButton
import digital.fischers.coinsaw.ui.components.CustomFloatingActionButtonType
import digital.fischers.coinsaw.ui.components.CustomNavigationBar
import digital.fischers.coinsaw.ui.components.DeleteModal
import digital.fischers.coinsaw.ui.viewModels.GroupEditViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun GroupEditScreen(
    onBackNavigation: (String) -> Unit,
    onAfterDeleteNavigation: () -> Unit,
    viewModel: GroupEditViewModel = hiltViewModel()
) {
    val groupName = viewModel.groupName
    val groupCurrency = viewModel.groupCurrency
    val loading = viewModel.loading
    val groupId = viewModel.groupId
    val showDeleteModal = viewModel.showDeleteModal

    val coroutineScope = rememberCoroutineScope()

    BaseScreen(
        blockingLoading = loading,
        appBar = {
            CustomNavigationBar(
                title = groupName,
                backNavigationText = null,
                backNavigation = { onBackNavigation(groupId) }
            )
        },
        floatingActionButton = {
            CustomFloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        viewModel.updateGroup()
                        onBackNavigation(groupId)
                    }
                },
                type = CustomFloatingActionButtonType.CONFIRM
            )
        },
        title = stringResource(id = R.string.screen_group_edit_title)
    ) {
        GroupEditForm(
            groupName = groupName,
            currency = groupCurrency,
            onNameChange = viewModel::onNameChanged,
            onCurrencyChange = viewModel::onCurrencyChanged
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.error)
            .clickable { viewModel.showDeleteModal() }
            .padding(16.dp)
,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.delete_group),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.background
                )
            )
        }
        if (showDeleteModal) {
            val context = LocalContext.current
            DeleteModal(
                title = stringResource(id = R.string.delete_group),
                description = stringResource(id = R.string.delete_group_confirmation),
                onConfirm = {
                    coroutineScope.launch {
                        viewModel.deleteGroup()
                    }.invokeOnCompletion {
                        disableAddBillShortcut(context = context, groupId = groupId)
                        viewModel.hideDeleteModal()
                        onAfterDeleteNavigation()
                    }
                },
                onCancel = { viewModel.hideDeleteModal() }
            )
        }
    }
}