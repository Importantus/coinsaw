package digital.fischers.coinsaw.ui.utils

sealed class CreateUiStates {
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
        val amount: Double = 0.0,
        val payerId: String = "",
        val splitting: List<Splitting> = emptyList()
    )

    data class Splitting(
        val userId: String = "",
        val percentage: Double = 0.0
    )
}