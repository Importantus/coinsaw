package digital.fischers.coinsaw.domain.repository

import digital.fischers.coinsaw.data.database.CalculatedTransaction
import kotlinx.coroutines.flow.Flow

interface CalculatedTransactionRepository {
    fun getAllByGroupIdStream(groupId: String): Flow<List<CalculatedTransaction>>
    fun getCalculatedTransactionStream(transactionId: String): Flow<CalculatedTransaction?>

    suspend fun getTotalBalanceByGroupIdAndUserId(groupId: String, userId: String): Flow<Double>

    suspend fun calculateForGroup(groupId: String)
    suspend fun insert(transaction: CalculatedTransaction)
    suspend fun insertAll(transactions: List<CalculatedTransaction>)
    suspend fun delete(transactionId: String)
}