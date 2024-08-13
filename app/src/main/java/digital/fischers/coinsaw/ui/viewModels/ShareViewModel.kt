package digital.fischers.coinsaw.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.coinsaw.data.remote.CreateShareRequest
import digital.fischers.coinsaw.data.remote.CreateShareResponse
import digital.fischers.coinsaw.data.remote.Share
import digital.fischers.coinsaw.domain.repository.RemoteRepository
import digital.fischers.coinsaw.ui.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    stateHandle: SavedStateHandle
) : ViewModel() {
    val groupId = stateHandle.get<String>(Screen.ARG_GROUP_ID)!!
    var loading by mutableStateOf(true)
        private set

    var errorLoadingShares: Int? by mutableStateOf(null)
        private set

    var errorCreatingShare: Int? by mutableStateOf(null)
        private set

    private var _existingShares = MutableStateFlow(emptyList<Share>())
    val existingShares = _existingShares.asStateFlow()

    private var _newShareName = MutableStateFlow("")
    val newShareName = _newShareName.asStateFlow()

    private var _newShareAdmin = MutableStateFlow(false)
    val newShareAdmin = _newShareAdmin.asStateFlow()

    private var _newShareMaxSessions = MutableStateFlow("1")
    val newShareMaxSessions = _newShareMaxSessions.asStateFlow()

    fun onNameChanged(name: String) {
        _newShareName.value = name
    }

    fun onAdminChanged(admin: Boolean) {
        _newShareAdmin.value = admin
    }

    fun onMaxSessionsChanged(maxSessions: String) {
        // Filter out all non-digit characters
        val numberString = maxSessions.take(6).filter { it.isDigit() }
        _newShareMaxSessions.value = numberString
    }

    suspend fun createShare(): CreateShareResponse? {
        loading = true
        val shareResponse = remoteRepository.createShare(
            groupId, CreateShareRequest(
                name = newShareName.value,
                admin = newShareAdmin.value,
                maxSessions = newShareMaxSessions.value.toInt()
            )
        )

        if (shareResponse.isSuccessful) {
            _newShareAdmin.value = false
            _newShareMaxSessions.value = "1"
            _newShareName.value = ""
        } else {
            errorCreatingShare = shareResponse.code()
        }
        loading = false
        return shareResponse.body()
    }

    private suspend fun loadAllShares(): List<Share>? {
        loading = true
        val sharesResponse = remoteRepository.getAllShares(groupId)

        if (!sharesResponse.isSuccessful) {
            errorLoadingShares = sharesResponse.code()
        }

        loading = false
        return sharesResponse.body()
    }

    init {
        viewModelScope.launch {
            _existingShares.value = loadAllShares() ?: emptyList()
        }
    }
}