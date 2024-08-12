package digital.fischers.coinsaw.ui.remote

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.ui.components.BaseScreen
import digital.fischers.coinsaw.ui.viewModels.InitialSyncViewModel

@Composable
fun InitialSyncScreen(
    onForwardNavigation: (String) -> Unit,
    viewModel: InitialSyncViewModel = hiltViewModel()
) {
    val ready = viewModel.ready
    val groupId = viewModel.groupId

    if (ready) {
        onForwardNavigation(groupId)
    }

    BaseScreen(appBar = { }) {
        Text(text = stringResource(id =R.string.initial_sync))
    }
}