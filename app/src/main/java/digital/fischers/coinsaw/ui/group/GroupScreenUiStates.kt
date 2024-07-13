package digital.fischers.coinsaw.ui.group

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

sealed class GroupScreenUiStates {
    data class Group(
        val name: String = "",
        val currency: String = ""
    )

    data class  User(
        val name: String = "",
        val isMe: Boolean = false
    )

    data class Bill(
        val name: String = "",
        val created: Long = 0,
        val amount: Double = 0.0,
        val payer: StateFlow<User>,
        val splitting: List<Splitting> = emptyList()
    )

    data class Splitting(
        val user: StateFlow<User>,
        val percentage: Double = 0.0
    )

    data class CalculatedTransaction(
        val amount: Double = 0.0,
        val payer: StateFlow<User>,
        val payee: StateFlow<User>
    )
}