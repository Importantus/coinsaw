package digital.fischers.coinsaw.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.coinsaw.domain.repository.GroupRepository
import digital.fischers.coinsaw.ui.utils.CreateUiStates
import javax.inject.Inject

@HiltViewModel
class NewGroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {
    var loading by mutableStateOf(false)
        private set

    var groupName by mutableStateOf("")
        private set

    var currency by mutableStateOf("€")
        private set

    fun onGroupNameChanged(name: String) {
        groupName = name
    }

    fun onCurrencyChanged(currency: String) {
        this.currency = currency
    }

    suspend fun createGroup(): String {
        loading = true
        val group = groupRepository.createGroup(
            CreateUiStates.Group(
                name = groupName,
                currency = currency
            )
        )
        loading = false
        return group.id
    }
}