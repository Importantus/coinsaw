package digital.fischers.coinsaw.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "bill")
data class Bill(
    @PrimaryKey val id: String,
    val name: String,
    val amount: Double,
    val isDeleted: Boolean,
    val createdAt: Long,
    val groupId: String,
    val userId: String,
    val splittings: List<Splitting>
)