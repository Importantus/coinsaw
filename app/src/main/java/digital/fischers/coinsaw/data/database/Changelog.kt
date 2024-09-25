package digital.fischers.coinsaw.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "changelog")
data class Changelog (
    @PrimaryKey val id: String,
    val timestamp: Long,
    @ColumnInfo(defaultValue = "null") val addedLocallyAt: Long?,
    val groupId: String,
    val synced: Boolean,
    val content: String
)