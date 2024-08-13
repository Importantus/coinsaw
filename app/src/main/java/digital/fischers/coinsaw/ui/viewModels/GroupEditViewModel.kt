package digital.fischers.coinsaw.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.coinsaw.domain.changelog.Payload
import digital.fischers.coinsaw.domain.repository.GroupRepository
import digital.fischers.coinsaw.ui.Screen
import digital.fischers.coinsaw.ui.group.GroupScreenUiStates
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupEditViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val groupId: String = savedStateHandle.get<String>(Screen.ARG_GROUP_ID)!!

    var groupName: String by mutableStateOf("")
        private set

    var groupCurrency: String by mutableStateOf("")
        private set

    var loading: Boolean by mutableStateOf(true)
        private set

    var showDeleteModal: Boolean by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            loadGroup()
        }
    }

    private suspend fun loadGroup() {
        val group = groupRepository.getGroupStream(groupId).firstOrNull()
        groupName = group?.name ?: ""
        groupCurrency = group?.currency ?: ""
        loading = false
    }

    fun onNameChanged(name: String) {
        groupName = name
    }

    fun onCurrencyChanged(currency: String) {
        groupCurrency = currency
    }

    fun showDeleteModal() {
        showDeleteModal = true
    }

    fun hideDeleteModal() {
        showDeleteModal = false
    }

    suspend fun updateGroup() {
        groupRepository.updateGroup(groupId, Payload.GroupSettings(
            name = groupName,
            currency = groupCurrency
        ))
    }

    suspend fun deleteGroup() {
        groupRepository.deleteGroup(groupId)
    }
}