package digital.fischers.coinsaw.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculatedTransactionDao {
    @Query("SELECT * FROM calculated_transactions")
    fun getAll(): Flow<List<CalculatedTransaction>>

    @Query("SELECT * FROM calculated_transactions WHERE id = :id")
    fun getById(id: String): Flow<CalculatedTransaction>

    @Query("SELECT * FROM calculated_transactions WHERE groupId = :groupId")
    fun getByGroupId(groupId: String): Flow<List<CalculatedTransaction>>

    @Query("SELECT * FROM calculated_transactions WHERE payerId = :payerId")
    fun getByPayerId(payerId: String): Flow<List<CalculatedTransaction>>

    @Query("SELECT * FROM calculated_transactions WHERE payeeId = :payeeId")
    fun getByPayeeId(payeeId: String): Flow<List<CalculatedTransaction>>

    @Query("SELECT * FROM calculated_transactions WHERE payerId = :payerId AND payeeId = :payeeId")
    fun getByPayerIdAndPayeeId(payerId: String, payeeId: String): Flow<CalculatedTransaction>

    @Insert
    suspend fun insert(transaction: CalculatedTransaction)

    @Insert
    suspend fun insertAll(transactions: List<CalculatedTransaction>)

    @Query("DELETE FROM calculated_transactions WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM calculated_transactions WHERE groupId = :groupId")
    suspend fun deleteAllByGroupId(groupId: String)
}