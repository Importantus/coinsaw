package digital.fischers.coinsaw.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.coinsaw.data.remote.APIError
import digital.fischers.coinsaw.data.remote.Session
import digital.fischers.coinsaw.domain.repository.RemoteRepository
import digital.fischers.coinsaw.ui.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import digital.fischers.coinsaw.data.remote.APIResult
import digital.fischers.coinsaw.domain.repository.GroupRepository
import digital.fischers.coinsaw.ui.group.GroupScreenUiStates
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SessionsViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val groupRepository: GroupRepository,
    stateHandle: SavedStateHandle
) : ViewModel() {
    val groupId = stateHandle.get<String>(Screen.ARG_GROUP_ID)!!

    var loading by mutableStateOf(true)
        private set

    var loadError: APIError? by mutableStateOf(null)
        private set

    var deleteError: APIError? by mutableStateOf(null)
        private set

    private var _sessions = MutableStateFlow(emptyList<Session>())
    val sessions = _sessions.asStateFlow()

    val currentSessionId = groupRepository.getGroupStream(groupId).map { group ->
        group?.sessionId
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ""
    )

    fun load() {
        viewModelScope.launch {
            loading = true
            var sessions = when (val sessionsResponse = remoteRepository.getAllSessions(groupId)) {
                is APIResult.Success -> {
                    loadError = null
                    sessionsResponse.data
                }
                is APIResult.Error -> {
                    loadError = sessionsResponse.exception
                    emptyList<Session>()
                }
            }

            sessions = sessions.sortedByDescending { it.last_active_timestamp }

            _sessions.value = sessions
            loading = false
        }
    }

    suspend fun deleteSession(id: String): Boolean {
        loading = true
        var success = false

        when (val deleteResponse = remoteRepository.deleteSession(groupId, id)) {
            is APIResult.Error -> {
                deleteError = deleteResponse.exception
            }
            is APIResult.Success -> {
                deleteError = null
                success = true
            }
        }

        loading = false
        return success
    }
}