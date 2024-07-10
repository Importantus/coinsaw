package digital.fischers.coinsaw.ui.utils

sealed class CreateUiStates {
    data class Group(
        var name: String = "",
        var currency: String = ""
    )

    data class  User(
        var name: String = "",
        var isMe: Boolean = false
    )

    data class Bill(
        var name: String = "",
        var amount: Double = 0.0,
        var payerId: String = "",
        var splitting: List<Splitting> = emptyList()
    )

    data class Splitting(
        var userId: String = "",
        var percentage: Double = 0.0
    )
}