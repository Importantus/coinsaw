package digital.fischers.coinsaw.ui.remote

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.ui.components.BaseScreen
import digital.fischers.coinsaw.ui.components.CustomFloatingActionButton
import digital.fischers.coinsaw.ui.components.CustomFloatingActionButtonType
import digital.fischers.coinsaw.ui.components.CustomNavigationBar
import digital.fischers.coinsaw.ui.components.CustomTextField
import digital.fischers.coinsaw.ui.viewModels.EnterShareTokenViewModel

@Composable
fun EnterShareTokenScreen(
    onBackNavigation: () -> Unit,
    onForwardNavigation: (String) -> Unit,
    viewModel: EnterShareTokenViewModel = hiltViewModel()
) {
    val shareToken by viewModel.shareToken.collectAsState()
    val wrongShareTokenError = viewModel.shareTokenDefault.isNotBlank()
    val groupMissingSessionError = viewModel.missingSessionError

    val title = if(groupMissingSessionError) {
        stringResource(id = R.string.screen_missing_token_title)
    } else {
        stringResource(id = R.string.join_group)
    }

    val desc = if(groupMissingSessionError) {
        stringResource(id = R.string.enter_missing_token_desc)
    } else {
        stringResource(id = R.string.enter_share_token_desc)
    }

    BaseScreen(
        title = if(groupMissingSessionError) {
            stringResource(id = R.string.screen_missing_token_title)
        } else {
            stringResource(id = R.string.screen_join_group_big_title)
        },
        appBar = {
        CustomNavigationBar(
            title = title,
            backNavigationText = null,
            backNavigation = { onBackNavigation() })
    },
        floatingActionButton = {
            CustomFloatingActionButton(
                onClick = {
                    onForwardNavigation(shareToken)
                }, type = CustomFloatingActionButtonType.NEXT
            )
        }
    ) {
        Column {
            Text(text = desc)
            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = shareToken,
                onValueChange = { viewModel.onShareTokenChanged(it) },
                label = stringResource(id = R.string.share_token),
                isError = wrongShareTokenError,
                placeholder = stringResource(id = R.string.paste_key_here)
            )
        }
    }
}