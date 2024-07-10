package digital.fischers.coinsaw.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.domain.repository.BillRepository
import digital.fischers.coinsaw.domain.repository.CalculatedTransactionRepository
import digital.fischers.coinsaw.domain.repository.GroupRepository
import digital.fischers.coinsaw.domain.repository.UserRepository
import digital.fischers.coinsaw.ui.home.HomeGroupUiState
import digital.fischers.coinsaw.ui.home.HomeTransactionUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val calculatedTransactionRepository: CalculatedTransactionRepository,
    private val billRepository: BillRepository
) : ViewModel() {
    val groups = groupRepository.getAllGroupsStream().map { groups ->
        groups.map { group ->
            val me =
                userRepository.getUserByGroupIdAndIsMeStream(group.id, isMe = true).firstOrNull()

            val members = userRepository.getAllUsersByGroupIdStream(group.id).firstOrNull()?.count()
                ?: 0

            val balance = calculatedTransactionRepository.getTotalBalanceByGroupIdAndUserId(
                group.id,
                me?.id ?: ""
            ).firstOrNull() ?: 0.0

            val lastTransactions = billRepository.getBillsByGroupAndIsDeletedStream(
                group.id,
                isDeleted = false
            ).map { bill ->
                bill.map {
                    HomeTransactionUiState(
                        name = it.name,
                        amount = it.amount,
                        payer = userRepository.getUserStream(it.userId).firstOrNull()?.name
                            ?: R.string.unknown_user.toString()
                    )
                }
            }.take(5)

            HomeGroupUiState(
                id = group.id,
                name = group.name,
                currency = group.currency,
                online = group.online,
                members = members,
                balance = balance,
                lastSync = group.lastSync,
                lastTransactions = lastTransactions
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyList()
    )

//    val groups = groupRepository.getAllGroupsStream().stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5_000L),
//        initialValue = emptyList()
//    )
}