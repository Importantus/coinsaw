package digital.fischers.coinsaw.domain.repository

import digital.fischers.coinsaw.data.database.Bill
import digital.fischers.coinsaw.domain.changelog.Payload
import digital.fischers.coinsaw.ui.utils.CreateUiStates
import kotlinx.coroutines.flow.Flow

interface BillRepository {
    fun getAllBillsStream(): Flow<List<Bill>>
    fun getBillsByGroupStream(groupId: String): Flow<List<Bill>>
    fun getBillsByGroupAndIsDeletedStream(groupId: String, isDeleted: Boolean): Flow<List<Bill>>
    fun getBillStream(billId: String): Flow<Bill?>

    suspend fun createBill(groupId: String, bill: CreateUiStates.Bill)
    suspend fun updateBill(groupId: String, changes: Payload.Bill)
}