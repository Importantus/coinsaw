package digital.fischers.coinsaw.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChangelogDao {
    @Query("SELECT * FROM changelog")
    fun getAll(): Flow<List<Changelog>>

    @Query("SELECT * FROM changelog WHERE id = :id")
    fun getEntry(id: String): Flow<Changelog>

    @Query("SELECT * FROM changelog WHERE groupId = :groupId")
    fun getEntriesByGroup(groupId: String): Flow<List<Changelog>>

    @Query("SELECT * FROM changelog WHERE groupId = :groupId AND timestamp > :timestamp")
    fun getEntriesByGroupYoungerThanTimestamp(groupId: String, timestamp: Long): Flow<List<Changelog>>

    @Query("SELECT * FROM changelog WHERE groupId = :groupId AND timestamp < :timestamp")
    fun getEntriesByGroupOlderThanTimestamp(groupId: String, timestamp: Long): Flow<List<Changelog>>

    @Query("SELECT * FROM changelog WHERE groupId = :groupId AND timestamp > :timestamp AND synced = 0")
    fun getEntriesByGroupYoungerThanTimestampNotSynced(groupId: String, timestamp: Long): Flow<List<Changelog>>

    @Query("SELECT * FROM changelog WHERE groupId = :groupId AND addedLocallyAt > :timestamp AND synced = 1")
    fun getEntriesByGroupLocallyAddedAfterTimestampSynced(groupId: String, timestamp: Long): Flow<List<Changelog>>

    @Insert
    suspend fun insert(entry: Changelog)

    @Query("DELETE FROM changelog WHERE groupId = :groupId")
    suspend fun deleteAllByGroupId(groupId: String)
}