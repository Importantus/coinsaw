package digital.fischers.coinsaw.ui.remote

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
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
    shareTokenDefaultValue: String = "",
    wrongShareTokenError: Boolean = false,
    viewModel: EnterShareTokenViewModel = hiltViewModel()
) {
    val shareToken by viewModel.shareToken.collectAsState()

    viewModel.onShareTokenChanged(shareTokenDefaultValue)

    BaseScreen(appBar = {
        CustomNavigationBar(
            title = stringResource(id = R.string.join_group),
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