package digital.fischers.coinsaw.ui.viewModels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.coinsaw.data.database.Group
import digital.fischers.coinsaw.data.remote.APIResult
import digital.fischers.coinsaw.domain.repository.BillRepository
import digital.fischers.coinsaw.domain.repository.CalculatedTransactionRepository
import digital.fischers.coinsaw.domain.repository.GroupRepository
import digital.fischers.coinsaw.domain.repository.RemoteRepository
import digital.fischers.coinsaw.domain.repository.UserRepository
import digital.fischers.coinsaw.ui.group.GroupScreenUiStates
import digital.fischers.coinsaw.ui.Screen
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val calculatedTransactionRepository: CalculatedTransactionRepository,
    private val billRepository: BillRepository,
    private val remoteRepository: RemoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val groupId: String = savedStateHandle.get<String>(Screen.ARG_GROUP_ID)!!

    var syncError by mutableStateOf(false)
        private set

    var syncing by mutableStateOf(false)
        private set

    val group = groupRepository.getGroupStream(groupId).map { group ->
        groupUIStateFromGroup(group)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = GroupScreenUiStates.Group()
    )

    val members = userRepository.getAllUsersByGroupIdStream(groupId).map { users ->
        users.map { user ->
            GroupScreenUiStates.User(
                name = user.name,
                isMe = user.isMe
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyList()
    )

    val calculatedTransactions = calculatedTransactionRepository.getAllByGroupIdStream(groupId).map { transactions ->
        transactions.map {
            GroupScreenUiStates.CalculatedTransaction(
                id = it.id,
                amount = it.amount,
                payer = getUserStateFlow(it.payerId),
                payee = getUserStateFlow(it.payeeId)
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyList()
    )

    private val me = userRepository.getUserByGroupIdAndIsMeStream(groupId, true).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = null
    )

    // Combine the me StateFlow with the bills StateFlow
    val bills = combine(
        me,
        billRepository.getBillsByGroupAndIsDeletedStream(groupId, isDeleted = false)
    ) { me, bills ->
        bills.map {
            var myShare = it.splittings.find { split -> split.userId == me?.id }?.percent
            if (me != null && it.userId == me.id) {
                myShare = 1.0 - (myShare ?: 0.0)
            }
            val myShareMultiplier = if (me != null && it.userId == me.id) 1 else -1
            GroupScreenUiStates.Bill(
                id = it.id,
                name = it.name,
                amount = it.amount,
                created = it.createdAt,
                myShare = it.amount * (myShare ?: 0.0) * myShareMultiplier,
                payer = getUserStateFlow(it.userId),
                // Only get splittings if title is empty (meaning it's a transaction)
                splitting = if (it.name.isBlank()) it.splittings.map { splitting ->
                    GroupScreenUiStates.Splitting(
                        user = getUserStateFlow(splitting.userId),
                        percentage = splitting.percent
                    )
                } else emptyList()
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyList()
    )

    init {
            viewModelScope.launch {
                val group = groupRepository.getGroupStream(groupId).map {
                    group -> groupUIStateFromGroup(group)
                }.first()
                pushAddBillShortcut(context, groupId, group.name)
            }
    }

    fun syncGroup(group: GroupScreenUiStates.Group) {
        viewModelScope.launch {
            if(group.isOnline && group.hasSession) {
                syncing = true
                val response = remoteRepository.syncGroup(groupId)
                syncError = response is APIResult.Error
                syncing = false
            }
        }
    }

    private fun groupUIStateFromGroup(group: Group?): GroupScreenUiStates.Group {
        return GroupScreenUiStates.Group(
            name = group?.name ?: "",
            currency = group?.currency ?: "",
            isOnline = group?.online ?: false,
            isAdmin = group?.admin ?: false,
            hasSession = group?.sessionId != null,
            lastSync = group?.lastSync ?: 0
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