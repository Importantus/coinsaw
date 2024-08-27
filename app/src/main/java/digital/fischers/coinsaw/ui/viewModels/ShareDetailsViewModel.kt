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
import digital.fischers.coinsaw.data.remote.ShareWithToken
import digital.fischers.coinsaw.domain.repository.RemoteRepository
import digital.fischers.coinsaw.ui.Screen
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareDetailsViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    stateHandle: SavedStateHandle
) : ViewModel() {
    val groupId = stateHandle.get<String>(Screen.ARG_GROUP_ID)!!
    val shareId = stateHandle.get<String>(Screen.ARG_SHARE_ID)!!

    var loading by mutableStateOf(false)
        private set

    var loadError: APIError? by mutableStateOf(null)
        private set

    var deleteError: APIError? by mutableStateOf(null)
        private set

    var share: ShareWithToken? by mutableStateOf(null)
        private set

    init {
        viewModelScope.launch {
            loadShare()
        }
    }

    suspend fun loadShare() {
        loading = true
        when (val shareResponse = remoteRepository.getShare(groupId, shareId)) {
            is APIResult.Error -> {
                loadError = shareResponse.exception
            }
            is APIResult.Success -> {
                loadError = null
                share = shareResponse.data
            }
        }
        loading = false
    }

    suspend fun deleteShare(): Boolean {
        loading = true

        val shareResponse = remoteRepository.deleteShare(groupId, shareId)

        when (shareResponse) {
            is APIResult.Error -> {
                deleteError = shareResponse.exception
            }
            is APIResult.Success -> {
                deleteError = null
            }
        }

        loading = false
        return shareResponse is APIResult.Success
    }
}