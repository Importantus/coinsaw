package digital.fischers.coinsaw.ui.viewModels

import android.graphics.BitmapFactory
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
import digital.fischers.coinsaw.data.remote.ShareWithToken
import digital.fischers.coinsaw.domain.repository.RemoteRepository
import digital.fischers.coinsaw.ui.Screen
import kotlinx.coroutines.launch
import qrcode.QRCode
import qrcode.color.Colors
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

    var networkError by mutableStateOf(false)
        private set

    var error: Int? by mutableStateOf(null)
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
                when (shareResponse.exception) {
                    is APIError.CustomError -> {
                        error = shareResponse.exception.code
                    }
                    APIError.NetworkError -> {
                        networkError = true
                    }
                    APIError.UnknownError -> {
                        networkError = true
                    }
                }
            }
            is APIResult.Success -> {
                error = null
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
                when (shareResponse.exception) {
                    is APIError.CustomError -> error = shareResponse.exception.code
                    APIError.NetworkError -> networkError = true
                    APIError.UnknownError -> networkError = true
                }
            }
            is APIResult.Success -> {
                error = null
            }
        }

        loading = false
        return shareResponse is APIResult.Success
    }
}