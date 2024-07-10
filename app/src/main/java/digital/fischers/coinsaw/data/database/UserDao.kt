package digital.fischers.coinsaw.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): Flow<List<User>>

    @Query("SELECT * FROM user WHERE id = :id")
    fun getById(id: String): Flow<User>

    @Query("SELECT * FROM user WHERE name = :name")
    fun getByName(name: String): Flow<User>

    @Query("SELECT * FROM user WHERE groupId = :groupId AND isMe = :isMe")
    fun getByGroupIdAndIsMe(groupId: String, isMe: Boolean): Flow<User>

    @Query("SELECT * FROM user WHERE groupId = :groupId")
    fun getByGroupId(groupId: String): Flow<List<User>>

    @Query("SELECT * FROM user WHERE groupId = :groupId AND isDeleted = :isDeleted")
    fun getByGroupIdAndIsDeleted(groupId: String, isDeleted: Boolean): Flow<List<User>>

    @Insert
    fun insert(user: User)

    @Update
    fun update(user: User)
}