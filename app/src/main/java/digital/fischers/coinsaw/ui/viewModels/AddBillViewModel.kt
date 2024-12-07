package digital.fischers.coinsaw.ui.viewModels

import android.icu.text.DecimalFormat
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.coinsaw.data.database.User
import digital.fischers.coinsaw.domain.repository.BillRepository
import digital.fischers.coinsaw.domain.repository.GroupRepository
import digital.fischers.coinsaw.domain.repository.UserRepository
import digital.fischers.coinsaw.ui.Screen
import digital.fischers.coinsaw.ui.utils.CreateUiStates
import digital.fischers.coinsaw.ui.utils.formatAsDecimal
import digital.fischers.coinsaw.ui.utils.roundHalfUp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.min

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

    val splittings = MutableStateFlow(emptyList<TempSplitting>())

    private var _newBillState = MutableStateFlow(CreateUiStates.Bill())
    val newBillState = _newBillState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.getUsersByGroupIdAndIsDeletedStream(groupId, false).firstOrNull()
                ?.let { users ->
                    calculateSplittings(users)
                }
        }

        viewModelScope.launch {
            val payerId = userRepository.getMeOrFirstUserByGroupIdStream(groupId).firstOrNull()?.id
                ?: ""
            _newBillState.value = newBillState.value.copy(
                payerId = payerId
            )
        }

        viewModelScope.launch {
            newBillState.collect {
                checkIfValid()
            }
        }
    }

    private fun calculateSplittings(users: List<User>) {
        val total = users.size
        val percentPerUser = (100.0 / total).roundHalfUp()
        val remainingPercentage = 100.0 - (percentPerUser * total)

        val randomUserId = users.random().id

        splittings.value = users.map {
            var percent = percentPerUser

            if (it.id == randomUserId && abs(remainingPercentage) >= 0.01) {
                percent += remainingPercentage
            }

            TempSplitting(
                userId = it.id,
                percentage = percent,
                enabled = true,
                edited = false
            )
        }
    }

    fun resetSplittings() {
        calculateSplittings(users.value)
    }

    fun getUserById(userId: String) = users.value.find { it.id == userId }

    fun onNameChanged(name: String) {
        _newBillState.value = _newBillState.value.copy(name = name)
//        checkIfValid()
    }

    fun onAmountChanged(value: Double) {
        _newBillState.value = _newBillState.value.copy(amount = value)
//        checkIfValid()
    }

    fun onPayerChanged(payerId: String) {
        _newBillState.value = _newBillState.value.copy(payerId = payerId)
//        checkIfValid()
    }

    fun onSplittingChanged(userId: String, value: Double) {
        var percentage = value
        if(percentage < 0.0) {
            percentage = 0.0
        } else if(percentage > 100.0) {
            percentage = 100.0
        }

        val newSplittings = splittings.value.toMutableList()
        val index = newSplittings.indexOfFirst { it.userId == userId }
        newSplittings[index] = newSplittings[index].copy(percentage = percentage, edited = true)

        // Update all other non edited splittings to keep the total at 100%
        val editedSplittings = newSplittings.filter { it.edited }
        val editedSum = editedSplittings.sumOf { it.percentage }

        var remainingSplittings = newSplittings.filter { !it.edited && it.percentage >= 0.0 && it.percentage <= 100.0 }
        val remainingSum = 100.0 - editedSum

        /**
         * TODO: Don't let the percents go below 0 or above 100
         * The remaining sum has to be distributed to the remaining, non-edited splittings
         * If all of these splittings are 0 or 100, the remaining sum has to be distributed to all of them
         */

        if(remainingSplittings.isNotEmpty()) {
            val remainingPerUser = remainingSum / remainingSplittings.size
            val remainingPerUserRounded = DecimalFormat("#.##").format(remainingPerUser).toDouble()

            newSplittings.forEachIndexed { i, splitting ->
                if (!splitting.edited) {
                    newSplittings[i] = splitting.copy(percentage = if(remainingPerUserRounded <= 0) 0.0 else remainingPerUserRounded)
                }
            }
        }

//        remainingSplittings = newSplittings.filter { !it.edited && it.percentage >= 0.0 && it.percentage <= 100.0 }
//
//        if(remainingSplittings.isEmpty() && remainingSum > 0.0) {
//            // Normalize edited splittings
//            val remainingSumRounded = DecimalFormat("#.##").format(remainingSum).toDouble()
//
//            Log.d("AddBillViewModel", "onSplittingChanged: $remainingSumRounded, ${editedSplittings.size}")
//
//            newSplittings.forEachIndexed { i, splitting ->
//                if (splitting.edited && splitting.userId != userId) {
//                    newSplittings[i] = splitting.copy(percentage = (newSplittings[i].percentage + (remainingSumRounded / (editedSplittings.size - 1))))
//                }
//            }
//        }

        splittings.value = newSplittings

        val percentageSum = splittings.value.sumOf { it.percentage }
        percentRemaining = if(100.0 - splittings.value.sumOf { it.percentage } > (-0.1)) abs(100.0 - percentageSum) else 100.0 - percentageSum

        checkIfValid()
    }

    private fun checkIfValid() {
        val nameIsValid =
            newBillState.value.name.isNotBlank() && newBillState.value.name.length <= 50
        val amountIsValid = try {
            newBillState.value.amount > 0
        } catch (e: NumberFormatException) {
            false
        }

        Log.d("AddBillViewModel", "checkIfValid: ${checkIfSplittingIs100Percent()} $nameIsValid $amountIsValid")

        valid = checkIfSplittingIs100Percent() && nameIsValid && amountIsValid
    }

    private fun checkIfSplittingIs100Percent(): Boolean {
        val sum = splittings.value.sumOf { it.percentage }
        return sum > 99.99 && sum < 100.01
    }

    suspend fun createBill() {
        loading = true
        if (!valid) {
            loading = false
            return
        }
        billRepository.createBill(groupId, newBillState.value.copy(
            splitting = splittings.value.map {
                CreateUiStates.Splitting(
                    userId = it.userId,
                    percentage = it.percentage.toString()
                )
            }
        ))
        loading = false
    }
}

data class TempSplitting(
    val userId: String,
    val percentage: Double,
    val enabled: Boolean,
    val edited: Boolean
)