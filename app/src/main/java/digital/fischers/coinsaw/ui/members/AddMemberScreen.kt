package digital.fischers.coinsaw.ui.members

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
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
import digital.fischers.coinsaw.ui.components.CustomTextField
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
        title = if (isInGroupCreationFlow) stringResource(id = R.string.screen_first_member_title)
        else stringResource(id = R.string.add_member)
    ) {
        CustomTextField(
            value = newUserState.name,
            onValueChange = { viewModel.onNameChanged(it) },
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
                checked = newUserState.isMe, onCheckedChange = { viewModel.onIsMeChanged(it) },
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
}