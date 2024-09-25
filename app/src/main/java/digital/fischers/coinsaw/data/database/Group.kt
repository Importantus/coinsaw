package digital.fischers.coinsaw.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class Group(
    @PrimaryKey val id: String,
    val name: String,
    val currency: String,
    val online: Boolean,
    val apiEndpoint: String?,
    val admin: Boolean,
    val accessToken: String?,
    val sessionId: String?,
    val lastSync: Long?,
    val createdAt: Long,
    @ColumnInfo(defaultValue = "null") val open: Boolean? = null,
    @ColumnInfo(defaultValue = "null") val lastOpenedAt: Long? = null
)
