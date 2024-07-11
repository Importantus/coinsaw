package digital.fischers.coinsaw.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.coinsaw.domain.repository.UserRepository
import digital.fischers.coinsaw.ui.utils.CreateUiStates
import digital.fischers.wisesplitpocv2.ui.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AddMemberViewModel @Inject constructor(
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val groupId = savedStateHandle.get<String>(Screen.ARG_GROUP_ID)!!

    var loading by mutableStateOf(false)
        private set

    private val _newUserState = MutableStateFlow(CreateUiStates.User())
    val newUserState = _newUserState.asStateFlow()

    fun onNameChanged(name: String) {
        _newUserState.value = _newUserState.value.copy(name = name)
    }

    fun onIsMeChanged(isMe: Boolean) {
        _newUserState.value = _newUserState.value.copy(isMe = isMe)
    }

    suspend fun createUser() {
        loading = true
        userRepository.createUser(groupId, _newUserState.value)
        loading = false
    }
}