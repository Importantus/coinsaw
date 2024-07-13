package digital.fischers.coinsaw.ui.viewModels

import android.icu.text.DecimalFormat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.coinsaw.domain.repository.BillRepository
import digital.fischers.coinsaw.domain.repository.GroupRepository
import digital.fischers.coinsaw.domain.repository.UserRepository
import digital.fischers.coinsaw.ui.Screen
import digital.fischers.coinsaw.ui.utils.CreateUiStates
import digital.fischers.coinsaw.ui.utils.formatAsDecimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBillViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val billRepository: BillRepository,
    private val groupRepository: GroupRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val groupId = savedStateHandle.get<String>(Screen.ARG_GROUP_ID)!!

    var loading by mutableStateOf(false)
        private set

    var valid by mutableStateOf(false)
        private set

    var percentRemaining by mutableDoubleStateOf(0.00)
        private set

    val group = groupRepository.getGroupStream(groupId).stateIn(
        scope = viewModelScope, started = WhileSubscribed(5_000L), initialValue = null
    )

    val users = userRepository.getUsersByGroupIdAndIsDeletedStream(groupId, false).stateIn(
        scope = viewModelScope, started = WhileSubscribed(5_000L), initialValue = emptyList()
    )

    private var _newBillState = MutableStateFlow(CreateUiStates.Bill())
    val newBillState = _newBillState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.getUsersByGroupIdAndIsDeletedStream(groupId, false).firstOrNull()
                ?.let { users ->
                    _newBillState.value = newBillState.value.copy(splitting = users.map {
                        CreateUiStates.Splitting(
                            userId = it.id, percentage = ((1.0 / users.size) * 100).toString().formatAsDecimal()
                        )
                    })
                }
        }

        viewModelScope.launch {
            val payerId = userRepository.getMeOrFirstUserByGroupIdStream(groupId).firstOrNull()?.id
                ?: ""
            _newBillState.value = newBillState.value.copy(
                payerId = payerId
            )
        }
    }

    fun getUserById(userId: String) = users.value.find { it.id == userId }

    fun onNameChanged(name: String) {
        _newBillState.value = _newBillState.value.copy(name = name)
        checkIfValid()
    }

    fun onAmountChanged(value: String) {
        val amount = value.formatAsDecimal(2)

        _newBillState.value = _newBillState.value.copy(amount = amount)
        checkIfValid()
    }

    fun onPayerChanged(payerId: String) {
        _newBillState.value = _newBillState.value.copy(payerId = payerId)
        checkIfValid()
    }

    fun onSplittingChanged(userId: String, percentage: String) {
        var amount = percentage.formatAsDecimal(2)
        // If the amount is empty, set it to 0
        if (amount.isBlank()) {
            amount = "0"
        }

        _newBillState.value = newBillState.value.copy(splitting = newBillState.value.splitting.map {
            if (it.userId == userId) {
                it.copy(percentage = amount)
            } else {
                it
            }
        })

        percentRemaining = 100.00 - newBillState.value.splitting.sumOf { it.percentage.toDouble() }

        checkIfValid()
    }

    private fun checkIfValid() {
        val nameIsValid =
            newBillState.value.name.isNotBlank() && newBillState.value.name.length <= 50
        val amountIsValid = try {
            newBillState.value.amount.isNotBlank() && newBillState.value.amount.toDouble() > 0
        } catch (e: NumberFormatException) {
            false
        }

        valid = checkIfSplittingIs100Percent() && nameIsValid && amountIsValid
    }

    private fun checkIfSplittingIs100Percent(): Boolean {
        return newBillState.value.splitting.sumOf { it.percentage.toDouble() } > 99.9 &&
                newBillState.value.splitting.sumOf { it.percentage.toDouble() } < 100.1
    }

    suspend fun createBill() {
        loading = true
        if (!valid) {
            loading = false
            return
        }
        billRepository.createBill(groupId, newBillState.value)
        loading = false
    }
}