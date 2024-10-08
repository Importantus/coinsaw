package digital.fischers.coinsaw.ui.group

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

sealed class GroupScreenUiStates {
    data class Group(
        val name: String = "",
        val currency: String = "",
        val isOnline: Boolean = false,
        val isAdmin: Boolean = false,
        val hasSession: Boolean = true,
        val lastSync: Long = 0,
    )

    data class  User(
        val id: String = "",
        val name: String = "",
        val isMe: Boolean = false
    )

    data class Bill(
        val id: String = "",
        val name: String = "",
        val created: Long = 0,
        val amount: Double = 0.0,
        val payer: StateFlow<User>? = null,
        val myShare: Double? = null,
        val splitting: List<Splitting> = emptyList()
    )

    data class Splitting(
        val user: StateFlow<User>,
        val percentage: Double = 0.0
    )

    data class CalculatedTransaction(
        val id: String = "",
        val amount: Double = 0.0,
        val payer: StateFlow<User>,
        val payee: StateFlow<User>
    )
}