package digital.fischers.coinsaw.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.coinsaw.data.remote.APIError
import digital.fischers.coinsaw.data.remote.APIResult
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

    var loadSharesError: APIError? by mutableStateOf(null)
        private set

    var createShareError: APIError? by mutableStateOf(null)
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

        when (shareResponse) {
            is APIResult.Error -> {
                createShareError = shareResponse.exception
                loading = false
                return null
            }
            is APIResult.Success -> {
                createShareError = null
                _newShareAdmin.value = false
                _newShareMaxSessions.value = "1"
                _newShareName.value = ""

                loading = false
                return shareResponse.data
            }
        }
    }

    private suspend fun loadAllShares(): List<Share>? {
        loading = true
        val shares = when (val sharesResponse = remoteRepository.getAllShares(groupId)) {
            is APIResult.Error -> {
                loadSharesError = sharesResponse.exception
                null
            }
            is APIResult.Success -> {
                loadSharesError = null
                sharesResponse.data
            }
        }

        loading = false
        return shares
    }

    init {
        viewModelScope.launch {
            _existingShares.value = loadAllShares() ?: emptyList()
        }
    }
}