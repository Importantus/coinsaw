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

    var error by mutableStateOf(false)
        private set

    var share: ShareWithToken? by mutableStateOf(null)
        private set

    init {
        viewModelScope.launch {
            try {
                Log.d("ShareDetailsViewModel", "groupId: $groupId, shareId: $shareId")
                loading = true
                share = remoteRepository.getShare(groupId, shareId)
                Log.d("ShareDetailsViewModel", "share: $share")
            } catch (e: Exception) {
                Log.d("ShareDetailsViewModel", "error: $e")
                error = true
            } finally {
                loading = false
            }
        }
    }

    suspend fun generateQRCode(content: String) {
        viewModelScope.launch {
            val qr = QRCode
                .ofRoundedSquares()
//                .withColor(color)
                .withBackgroundColor(Colors.TRANSPARENT)
                .withSize(30).build(content)
            val bytes = qr.renderToBytes()
            BitmapFactory.decodeByteArray(
                bytes,
                0,
                bytes.size
            )
        }.invokeOnCompletion {

        }

    }

    suspend fun deleteShare(): Boolean {
        try {
            loading = true
            remoteRepository.deleteShare(groupId, shareId)
            return true
        } catch (e: Exception) {
            error = true
        } finally {
            loading = false
        }
        return false
    }
}