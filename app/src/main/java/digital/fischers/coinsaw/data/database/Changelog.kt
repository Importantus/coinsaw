package digital.fischers.coinsaw.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "changelog")
data class Changelog (
    @PrimaryKey val id: String,
    val timestamp: Long,
    val groupId: String,
    val synced: Boolean,
    val content: String
)