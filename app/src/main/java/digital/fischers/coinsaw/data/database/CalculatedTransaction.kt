package digital.fischers.coinsaw.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculated_transactions")
data class CalculatedTransaction(
    @PrimaryKey val id: String,
    val groupId: String,
    val amount: Double,
    // The person who has to pay the bill
    val payerId: String,
    // The person receives the bill
    val payeeId: String
)
