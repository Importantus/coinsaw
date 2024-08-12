package digital.fischers.coinsaw.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.coinsaw.data.remote.CreateGroupResponse
import digital.fischers.coinsaw.domain.repository.RemoteRepository
import digital.fischers.coinsaw.ui.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MakeOnlineViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val groupId: String = savedStateHandle.get<String>(Screen.ARG_GROUP_ID)!!

    var loading by mutableStateOf(false)
        private set

    var wrongServerUrl by mutableStateOf(false)
        private set

    private val _serverUrlState = MutableStateFlow("")
    val serverUrlState = _serverUrlState.asStateFlow()

    fun onServerUrlChanged(serverUrl: String) {
        _serverUrlState.value = serverUrl
    }

    suspend fun makeOnline(): CreateGroupResponse? {
        loading = true
        var response: CreateGroupResponse? = null
        try {
            response = remoteRepository.createGroup(groupId, _serverUrlState.value)
        } catch (e: Exception) {
            wrongServerUrl = true
        }
        loading = false
        return response
    }
}