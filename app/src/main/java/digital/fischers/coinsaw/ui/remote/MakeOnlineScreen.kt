package digital.fischers.coinsaw.ui.remote

import android.util.Log
import androidx.compose.material3.FloatingActionButton
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
import digital.fischers.coinsaw.ui.components.CustomTextField
import digital.fischers.coinsaw.ui.viewModels.MakeOnlineViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun MakeOnlineScreen(
    onBackNavigation: () -> Unit,
    onForwardNavigation: (String, String) -> Unit,
    makeOnlineViewModel: MakeOnlineViewModel = hiltViewModel()
) {
    val groupId = makeOnlineViewModel.groupId
    val serverUrl by makeOnlineViewModel.serverUrlState.collectAsState()
    val loading = makeOnlineViewModel.loading
    val wrongServerUrl = makeOnlineViewModel.wrongServerUrl

    val coroutineScope = rememberCoroutineScope()

    BaseScreen(
        appBar = {
            CustomNavigationBar(
                title = stringResource(id = R.string.screen_make_online_title),
                backNavigationText = null,
                backNavigation = { onBackNavigation() }
            )
        },
        blockingLoading = loading,
        title = stringResource(id = R.string.screen_make_online_title),
        floatingActionButton = {
            if (serverUrl.isNotEmpty()) {
                CustomFloatingActionButton(onClick = {
                    coroutineScope.launch {
                        val response = makeOnlineViewModel.makeOnline()
                        Log.d("MakeOnlineScreen", "response: $response")
                        if (response != null && !wrongServerUrl) {
                            onForwardNavigation(groupId, response.recoveryToken)
                        }
                    }
                }, type = CustomFloatingActionButtonType.NEXT)
            }
        }
    ) {
        CustomTextField(
            value = serverUrl,
            onValueChange = { makeOnlineViewModel.onServerUrlChanged(it) },
            placeholder = stringResource(id = R.string.server_url_example),
            label = stringResource(id = R.string.server_url),
        )
    }
}