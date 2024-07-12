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
        bills.map {
            GroupScreenUiStates.Bill(
                name = it.name,
                amount = it.amount,
                created = it.createdAt,
                payer = getUserStateFlow(it.userId),
                splitting = it.splittings.map { split ->
                    GroupScreenUiStates.Splitting(
                        percentage = split.percent,
                        user = getUserStateFlow(split.userId)
                    )
                }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyList()
    )

    private fun getUserStateFlow(userId: String) = userRepository.getUserStream(userId).map { user ->
        GroupScreenUiStates.User(
            name = user?.name ?: "",
            isMe = user?.isMe ?: false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = GroupScreenUiStates.User()
    )
}