package digital.fischers.coinsaw.ui.home

import kotlinx.coroutines.flow.Flow

data class HomeGroupUiState(
    val id: String,
    val name: String,
    val members: Number,
    val online: Boolean,
    val lastSync: Long?,
    val balance: Double,
    val currency: String,
    val lastTransactions: Flow<List<HomeTransactionUiState>>
)

data class HomeTransactionUiState(
    val name: String,
    val amount: Double,
    val payer: String,
)
