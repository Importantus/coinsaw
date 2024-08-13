package digital.fischers.coinsaw.ui.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.coinsaw.data.remote.APIError
import digital.fischers.coinsaw.data.remote.APIResult
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
    var ready by mutableStateOf(false)
        private set

    var sessionError: APIError? by mutableStateOf(null)
        private set

    var syncError: APIError? by mutableStateOf(null)
        private set

    val shareToken: String = savedStateHandle.get<String>(Screen.ARG_SHARE_TOKEN)!!
    val groupId: String? = try {
        JSONObject(decodeToken(shareToken)).getString("groupId")
    } catch (e: Exception) {
        sessionError = APIError.UnknownError
        null
    }

    init {
        viewModelScope.launch {
            if(groupId != null) {
                val sessionResponse = remoteRepository.createSession(
                    CreateSessionRequest(
                        shareToken,
                        android.os.Build.MODEL
                    )
                )

                when (sessionResponse) {
                    is APIResult.Success -> {
                        when (val syncResponse = remoteRepository.syncGroup(groupId)) {
                            is APIResult.Success -> {
                                ready = true
                            }
                            is APIResult.Error -> {
                                syncError = syncResponse.exception
                            }
                        }
                    }
                    is APIResult.Error -> {
                        sessionError = sessionResponse.exception
                    }
                }
            }
        }
    }
}