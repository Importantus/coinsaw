package digital.fischers.coinsaw.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.coinsaw.data.database.User
import digital.fischers.coinsaw.domain.changelog.Payload
import digital.fischers.coinsaw.domain.repository.UserRepository
import digital.fischers.coinsaw.ui.Screen
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditMemberViewModel @Inject constructor(
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val groupId = savedStateHandle.get<String>(Screen.ARG_GROUP_ID)!!
    val userId = savedStateHandle.get<String>(Screen.ARG_USER_ID)!!

    init {
        viewModelScope.launch {
            loadUser()
        }
    }

    private var oldState: User? = null

    private suspend fun loadUser() {
        val user = userRepository.getUserStream(userId).firstOrNull()
        userName = user?.name ?: ""
        isMe = user?.isMe ?: false
        oldState = user
        loading = false
    }

    var userName by mutableStateOf("")
        private set

    var isMe by mutableStateOf(false)
        private set

    var loading by mutableStateOf(true)
        private set

    suspend fun deleteUser() {
        userRepository.updateUser(
            groupId = groupId,
            Payload.User(
                id = userId,
                isDeleted = true
            )
        )
    }

    fun onNameChanged(name: String) {
        userName = name
    }

    fun onIsMeChanged(isMe: Boolean) {
        this.isMe = isMe
    }

    suspend fun saveChanges() {
        loading = true
        updateIsMe()
        updateUser()
        loading = false
    }

    private suspend fun updateIsMe() {
        if(isMe != oldState?.isMe) {
            userRepository.setUserAsMe(groupId, userId, isMe)
        }
    }

    private suspend fun updateUser() {
        if(userName != oldState?.name) {
            userRepository.updateUser(
                groupId = groupId,
                Payload.User(
                    id = userId,
                    name = userName
                )
            )
        }
    }
}