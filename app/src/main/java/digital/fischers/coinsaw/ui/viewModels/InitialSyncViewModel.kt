package digital.fischers.coinsaw.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.coinsaw.data.remote.CreateSessionRequest
import digital.fischers.coinsaw.data.util.decodeToken
import digital.fischers.coinsaw.domain.repository.RemoteRepository
import digital.fischers.coinsaw.ui.Screen
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class InitialSyncViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val shareToken: String = savedStateHandle.get<String>(Screen.ARG_SHARE_TOKEN)!!
    val groupId: String = JSONObject(decodeToken(shareToken)).getString("groupId")

    var ready by mutableStateOf(false)
        private set

    var error by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            try {
                remoteRepository.createSession(CreateSessionRequest(shareToken, android.os.Build.MODEL))
                remoteRepository.syncGroup(groupId)
                ready = true
            } catch (e: Exception) {
                error = true
            }
        }
    }
}