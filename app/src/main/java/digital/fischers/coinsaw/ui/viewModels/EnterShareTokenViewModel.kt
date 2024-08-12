package digital.fischers.coinsaw.ui.viewModels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.coinsaw.domain.repository.RemoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EnterShareTokenViewModel @Inject constructor(
    remoteRepository: RemoteRepository
) : ViewModel() {
    val _shareToken = MutableStateFlow("")
    val shareToken = _shareToken.asStateFlow()

    fun onShareTokenChanged(shareToken: String) {
        _shareToken.value = shareToken
    }
}