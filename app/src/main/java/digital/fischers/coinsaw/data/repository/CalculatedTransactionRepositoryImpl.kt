package digital.fischers.coinsaw.data.repository

import digital.fischers.coinsaw.data.database.BillDao
import digital.fischers.coinsaw.data.database.CalculatedTransaction
import digital.fischers.coinsaw.data.database.CalculatedTransactionDao
import digital.fischers.coinsaw.data.util.calculateTransactions
import digital.fischers.coinsaw.domain.repository.BillRepository
import digital.fischers.coinsaw.domain.repository.CalculatedTransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.reduce
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
        return billDao.getAllBillsByGroupAndIsDeleted(groupId, isDeleted = false)
            .map { bills ->
                return@map bills.map { bill ->
                    if (bill.userId == userId) {
                        bill.amount - ((bill.amount * (bill.splittings.find { it.userId == userId }?.percent ?: 0.0)))
                    } else {
                        0.0 - bill.amount * (bill.splittings.find { it.userId == userId }?.percent ?: 0.0)
                    }
                }.sumOf { it }
            }
    }

    override suspend fun calculateForGroup(groupId: String) {
        billDao.getAllBillsByGroupAndIsDeleted(groupId, isDeleted = false)
            .collect { bills ->
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