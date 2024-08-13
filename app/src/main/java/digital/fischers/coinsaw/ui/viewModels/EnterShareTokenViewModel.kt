package digital.fischers.coinsaw.ui.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.coinsaw.domain.repository.RemoteRepository
import digital.fischers.coinsaw.ui.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EnterShareTokenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val shareTokenDefault = savedStateHandle.get<String>(Screen.ARG_SHARE_TOKEN_ERROR)!!
    val missingSessionError = savedStateHandle.get<String>(Screen.ARG_GROUP_MISSING_SESSION_ERROR)!! == "true"

    val _shareToken = MutableStateFlow(shareTokenDefault)
    val shareToken = _shareToken.asStateFlow()

    fun onShareTokenChanged(shareToken: String) {
        _shareToken.value = shareToken
    }
}