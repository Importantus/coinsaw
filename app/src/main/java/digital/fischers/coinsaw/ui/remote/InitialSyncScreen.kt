package digital.fischers.coinsaw.ui.remote

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.ui.components.BaseScreen
import digital.fischers.coinsaw.ui.components.CustomErrorAlert
import digital.fischers.coinsaw.ui.viewModels.InitialSyncViewModel

@Composable
fun InitialSyncScreen(
    onForwardNavigation: (String) -> Unit,
    onSyncErrorBackBackwardNavigation: () -> Unit,
    onSessionErrorBackwardNavigation: (shareToken: String, errorMessage: String) -> Unit,
    viewModel: InitialSyncViewModel = hiltViewModel()
) {
    val ready = viewModel.ready
    val groupId = viewModel.groupId
    val shareToken = viewModel.shareToken

    val sessionError = viewModel.sessionError
    val syncError = viewModel.syncError

    if (ready && groupId != null) {
        onForwardNavigation(groupId)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.sync(context)
        }
    }

    BaseScreen(appBar = { }) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(id = R.string.initial_sync) + "..", textAlign = TextAlign.Center)
        }

        if (sessionError != null) {
            CustomErrorAlert(
                error = sessionError,
                customFallbackTitle = stringResource(id = R.string.incorrect_share_token),
                customFallbackMessage = stringResource(
                    id = R.string.incorrect_share_token_desc
                ),
                onConfirm = { onSessionErrorBackwardNavigation(shareToken, "") }
            )
        }

        if (syncError != null) {
            CustomErrorAlert(
                error = syncError,
                onConfirm = { onSyncErrorBackBackwardNavigation() }
            )
        }
    }
}