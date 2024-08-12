package digital.fischers.coinsaw.ui.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalGraphicsContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.coinsaw.data.remote.CreateShareRequest
import digital.fischers.coinsaw.data.remote.CreateShareResponse
import digital.fischers.coinsaw.data.remote.Share
import digital.fischers.coinsaw.domain.repository.RemoteRepository
import digital.fischers.coinsaw.ui.Screen
import kotlinx.coroutines.coroutineScope
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

    var errorLoadingShares by mutableStateOf(false)
        private set

    var errorCreatingShare by mutableStateOf(false)
        private set

    private var _existingShares = MutableStateFlow(emptyList<Share>())
    val existingShares = _existingShares.asStateFlow()

    private var _newShareName = MutableStateFlow("")
    val newShareName = _newShareName.asStateFlow()

    private var _newShareAdmin = MutableStateFlow(false)
    val newShareAdmin = _newShareAdmin.asStateFlow()

    private var _newShareMaxSessions = MutableStateFlow(1)
    val newShareMaxSessions = _newShareMaxSessions.asStateFlow()

    fun onNameChanged(name: String) {
        _newShareName.value = name
    }

    fun onAdminChanged(admin: Boolean) {
        _newShareAdmin.value = admin
    }

    fun onMaxSessionsChanged(maxSessions: String) {
        // Filter out all non-digit characters
        val numberString = maxSessions.filter { it.isDigit() }
        _newShareMaxSessions.value = numberString.toInt()
    }

    suspend fun createShare(): CreateShareResponse? {
        loading = true
        try {
            val share = remoteRepository.createShare(groupId, CreateShareRequest(
                name = newShareName.value,
                admin = newShareAdmin.value,
                maxSessions = newShareMaxSessions.value
            ))

            _newShareAdmin.value = false
            _newShareMaxSessions.value = 1
            _newShareName.value = ""

            loading = false
            errorCreatingShare = false
            return share
        } catch (e: Exception) {
            errorCreatingShare = true
            Log.d("ShareViewModel", "Error creating share", e)
            loading = false
        }
        return null
    }

    init {
        viewModelScope.launch {
            try {
                loading = true
                _existingShares.value = remoteRepository.getAllShares(groupId)
                loading = false
                errorLoadingShares = false
            } catch (e: Exception) {
                errorLoadingShares = true
                Log.d("ShareViewModel", "Error loading shares", e)
                loading = false
            }
        }
    }
}