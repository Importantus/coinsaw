package digital.fischers.coinsaw.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.coinsaw.data.database.Group
import digital.fischers.coinsaw.domain.changelog.Payload
import digital.fischers.coinsaw.domain.repository.BillRepository
import digital.fischers.coinsaw.domain.repository.GroupRepository
import digital.fischers.coinsaw.domain.repository.UserRepository
import digital.fischers.coinsaw.ui.Screen
import digital.fischers.coinsaw.ui.group.GroupScreenUiStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BillDetailsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val billRepository: BillRepository,
    private val groupRepository: GroupRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val groupId = savedStateHandle.get<String>(Screen.ARG_GROUP_ID)!!
    val billId = savedStateHandle.get<String>(Screen.ARG_BILL_ID)!!

    var loading by mutableStateOf(false)
        private set

    val group = groupRepository.getGroupStream(groupId).map { group ->
        groupUIStateFromGroup(group)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = GroupScreenUiStates.Group()
    )

    val bill = billRepository.getBillStream(billId).map { bill ->
        bill?.let {
            GroupScreenUiStates.Bill(
                created = it.createdAt,
                name = it.name,
                amount = it.amount,
                payer = getUserStateFlow(it.userId),
                splitting = it.splittings.map { splitting ->
                    GroupScreenUiStates.Splitting(
                        user = getUserStateFlow(splitting.userId),
                        percentage = splitting.percent
                    )
                }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = GroupScreenUiStates.Bill()
    )

    suspend fun deleteBill() {
        loading = true
        billRepository.deleteBill(billId)
        loading = false
    }

    private fun groupUIStateFromGroup(group: Group?): GroupScreenUiStates.Group {
        return GroupScreenUiStates.Group(
            name = group?.name ?: "",
            currency = group?.currency ?: "",
            isOnline = group?.online ?: false,
            isAdmin = group?.admin ?: false,
            hasSession = group?.sessionId != null
        )
    }

    private fun getUserStateFlow(userId: String) = userRepository.getUserStream(userId).map { user ->
        GroupScreenUiStates.User(
            id = user?.id ?: "",
            name = user?.name ?: "",
            isMe = user?.isMe ?: false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = GroupScreenUiStates.User()
    )
}