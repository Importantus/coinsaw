package digital.fischers.coinsaw.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val isDeleted: Boolean,
    val isMe: Boolean,
    val createdAt: Long,
    val groupId: String
)
