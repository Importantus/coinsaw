package digital.fischers.coinsaw.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {
    @Query("SELECT * FROM bill WHERE isDeleted = :isDeleted ORDER BY createdAt DESC")
    fun getAllBills(isDeleted: Boolean = false): Flow<List<Bill>>

    @Query("SELECT * FROM bill WHERE groupId = :groupId ORDER BY createdAt DESC")
    fun getAllBillsByGroup(groupId: String): Flow<List<Bill>>

    @Query("SELECT * FROM bill WHERE groupId = :groupId AND isDeleted = :isDeleted ORDER BY createdAt DESC")
    fun getAllBillsByGroupAndIsDeleted(groupId: String, isDeleted: Boolean = false): Flow<List<Bill>>

    @Query("SELECT * FROM bill WHERE id = :billId")
    fun getBillById(billId: String): Flow<Bill>

    @Query("SELECT * FROM bill WHERE userId = :userId AND isDeleted = :isDeleted ORDER BY createdAt DESC")
    fun getBillsByUserId(userId: String, isDeleted: Boolean = false): Flow<List<Bill>>

    @Insert
    suspend fun insertBill(bill: Bill)

    @Update
    suspend fun updateBill(bill: Bill)

    @Query("DELETE FROM bill WHERE groupId = :groupId")
    suspend fun deleteAllByGroupId(groupId: String)
}