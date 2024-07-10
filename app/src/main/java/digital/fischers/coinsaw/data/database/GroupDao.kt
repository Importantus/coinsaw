package digital.fischers.coinsaw.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Query("SELECT * FROM groups ORDER BY name ASC")
    fun getGroups(): Flow<List<Group>>

    @Query("SELECT * FROM groups WHERE id = :groupId")
    fun getGroup(groupId: String): Flow<Group>

    @Query("SELECT * FROM groups WHERE online = 1")
    fun getOnlineGroups(): Flow<List<Group>>

    @Query("SELECT * FROM groups WHERE online = 0")
    fun getOfflineGroups(): Flow<List<Group>>

    @Insert
    suspend fun insert(group: Group)

    @Update
    suspend fun update(group: Group)

    @Query("DELETE FROM groups WHERE id = :groupId")
    suspend fun delete(groupId: String)
}
