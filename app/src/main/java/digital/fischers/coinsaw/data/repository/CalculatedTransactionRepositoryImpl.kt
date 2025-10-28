package digital.fischers.coinsaw.data.repository

import digital.fischers.coinsaw.data.database.BillDao
import digital.fischers.coinsaw.data.database.CalculatedTransaction
import digital.fischers.coinsaw.data.database.CalculatedTransactionDao
import digital.fischers.coinsaw.data.util.calculateTransactions
import digital.fischers.coinsaw.domain.repository.CalculatedTransactionRepository
import digital.fischers.coinsaw.ui.utils.roundHalfUp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CalculatedTransactionRepositoryImpl @Inject constructor(
    private val calculatedTransactionDao: CalculatedTransactionDao,
    private val billDao: BillDao
) : CalculatedTransactionRepository {
    override fun getAllByGroupIdStream(groupId: String): Flow<List<CalculatedTransaction>> {
        return calculatedTransactionDao.getByGroupId(groupId)
    }

    override fun getCalculatedTransactionStream(transactionId: String): Flow<CalculatedTransaction?> {
        return calculatedTransactionDao.getById(transactionId)
    }

    override suspend fun getTotalBalanceByGroupIdAndUserId(
        groupId: String,
        userId: String
    ): Flow<Double> {
        return getAllByGroupIdStream(groupId).map { transactions ->
            transactions.filter { it.payerId == userId || it.payeeId == userId }
                .fold(0.0) { acc, transaction ->
                    if (transaction.payerId == userId) {
                        acc - transaction.amount
                    } else {
                        acc + transaction.amount
                    }
                }.roundHalfUp()
        }
    }

    override suspend fun calculateForGroup(groupId: String) {
        calculatedTransactionDao.deleteAllByGroupId(groupId)

        billDao.getAllBillsByGroupAndIsDeleted(groupId, isDeleted = false)
            .first().let { bills ->
                insertAll(calculateTransactions(bills, groupId))
            }
    }

    override suspend fun insert(transaction: CalculatedTransaction) {
        calculatedTransactionDao.insert(transaction)
    }

    override suspend fun insertAll(transactions: List<CalculatedTransaction>) {
        calculatedTransactionDao.insertAll(transactions)
    }

    override suspend fun delete(transactionId: String) {
        calculatedTransactionDao.delete(transactionId)
    }
}