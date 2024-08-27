package digital.fischers.coinsaw.ui.remote

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.data.remote.APIError
import digital.fischers.coinsaw.ui.components.BaseScreen
import digital.fischers.coinsaw.ui.components.CustomErrorAlert
import digital.fischers.coinsaw.ui.components.CustomFloatingActionButton
import digital.fischers.coinsaw.ui.components.CustomFloatingActionButtonType
import digital.fischers.coinsaw.ui.components.CustomNavigationBar
import digital.fischers.coinsaw.ui.components.CustomTextField
import digital.fischers.coinsaw.ui.components.ErrorBox
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
                        if (response != null) {
                            onForwardNavigation(groupId, response.recoveryToken)
                        }
                    }
                }, type = CustomFloatingActionButtonType.NEXT)
            }
        }
    ) {
        if (wrongServerUrl) {
            ErrorBox(message = stringResource(id = R.string.wrong_url_err))
            Spacer(modifier = Modifier.height(12.dp))
        }

        CustomTextField(
            value = serverUrl,
            isError = wrongServerUrl,
            onValueChange = { makeOnlineViewModel.onServerUrlChanged(it) },
            placeholder = stringResource(id = R.string.server_url_example),
            label = stringResource(id = R.string.server_url),
        )

    }
}