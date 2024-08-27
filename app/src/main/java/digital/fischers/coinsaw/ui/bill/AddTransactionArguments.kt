package digital.fischers.coinsaw.ui.bill

data class AddTransactionArguments(
    val payerId: String? = null,
    val payeeId: String? = null,
    val amount: String? = null
)
