package digital.fischers.coinsaw.ui.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.coinsaw.domain.repository.BillRepository
import digital.fischers.coinsaw.domain.repository.CalculatedTransactionRepository
import digital.fischers.coinsaw.domain.repository.GroupRepository
import digital.fischers.coinsaw.domain.repository.UserRepository
import digital.fischers.coinsaw.ui.group.GroupScreenUiStates
import digital.fischers.coinsaw.ui.Screen
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val calculatedTransactionRepository: CalculatedTransactionRepository,
    private val billRepository: BillRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val groupId: String = savedStateHandle.get<String>(Screen.ARG_GROUP_ID)!!

    val group = groupRepository.getGroupStream(groupId).map { group ->
        GroupScreenUiStates.Group(
            name = group?.name ?: "",
            currency = group?.currency ?: "",
        )
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

    val bills = billRepository.getBillsByGroupAndIsDeletedStream(groupId, isDeleted = false).map { bills ->
        val me = userRepository.getUserByGroupIdAndIsMeStream(groupId, true).firstOrNull()
        bills.map {
            val myShare = it.splittings.find { split -> split.userId == me?.id }?.percent
            val myShareMultiplier = if(me != null && it.userId == me.id) 1 else -1
            GroupScreenUiStates.Bill(
                id = it.id,
                name = it.name,
                amount = it.amount,
                created = it.createdAt,
                myShare = if (myShare != null) it.amount * myShare * myShareMultiplier else null,
                payer = getUserStateFlow(it.userId),
                // Only get splittings if title is empty (meaning it's a transaction)
                splitting = if(it.name.isBlank()) it.splittings.map { splitting ->
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