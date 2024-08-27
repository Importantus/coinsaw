package digital.fischers.coinsaw.ui.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.coinsaw.domain.repository.BillRepository
import digital.fischers.coinsaw.domain.repository.UserRepository
import digital.fischers.coinsaw.ui.Screen
import digital.fischers.coinsaw.ui.bill.AddTransactionArguments
import digital.fischers.coinsaw.ui.utils.CreateUiStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val billRepository: BillRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val groupId = savedStateHandle.get<String>(Screen.ARG_GROUP_ID)!!
    private val args = Gson().fromJson<AddTransactionArguments>(
        savedStateHandle.get<String>(Screen.ARGS_CREATE_TRANSACTION)!!,
        AddTransactionArguments::class.java
    )

    var loading by mutableStateOf(false)
        private set

    var error by mutableStateOf<Boolean>(false)
        private set

    val users = userRepository.getUsersByGroupIdAndIsDeletedStream(groupId, false).stateIn(
        scope = viewModelScope, started = WhileSubscribed(5_000L), initialValue = emptyList()
    )

    private var _newTransactionState = MutableStateFlow(CreateUiStates.Bill())
    val newTransactionState = _newTransactionState.asStateFlow()

    init {
        Log.d("AddTransactionViewModel", "AddTransactionViewModel: $args")

        if (args.payeeId != null) {
            setPayeeId(args.payeeId)
        } else {
            setPayeeId(users.value.first().id)
        }

        if (args.payerId != null) {
            setPayerId(args.payerId)
        } else {
            setPayerId(users.value.last().id)
        }

        if (args.amount != null) {
            setAmount(args.amount)
        } else {
            setAmount("0.00")
        }
    }

    fun getPayee() = users.value.find { it.id == newTransactionState.value.splitting[0].userId }

    fun getPayer() = users.value.find { it.id == newTransactionState.value.payerId }

    // Payee is the user who will receive the money
    fun setPayeeId(userId: String) {
        _newTransactionState.value = _newTransactionState.value.copy(
            splitting = listOf(
                CreateUiStates.Splitting(
                    userId = userId,
                    percentage = "100"
                )
            )
        )
    }

    fun setPayerId(userId: String) {
        _newTransactionState.value = _newTransactionState.value.copy(payerId = userId)
    }

    fun setAmount(amount: String) {
        _newTransactionState.value = _newTransactionState.value.copy(amount = amount)
    }

    private fun checkIfValid(): Boolean {
        return try {
            newTransactionState.value.amount.isNotBlank() && newTransactionState.value.amount.toDouble() > 0
        } catch (e: NumberFormatException) {
            false
        }
    }

    suspend fun createTransaction(): Boolean {
        var success = false
        loading = true
        if (!checkIfValid()) {
            loading = false
            error = true
        } else {
            try {
                billRepository.createBill(groupId, newTransactionState.value)
                error = false
                success = true
            } catch (e: Exception) {
                error = true
            }
        }
        loading = false
        return success
    }
}