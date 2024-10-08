package digital.fischers.coinsaw.data.repository

import digital.fischers.coinsaw.data.database.Bill
import digital.fischers.coinsaw.data.database.BillDao
import digital.fischers.coinsaw.domain.changelog.ChangelogProcessor
import digital.fischers.coinsaw.domain.changelog.Entry
import digital.fischers.coinsaw.domain.changelog.EntryAction
import digital.fischers.coinsaw.domain.changelog.EntryType
import digital.fischers.coinsaw.domain.changelog.Payload
import digital.fischers.coinsaw.domain.repository.BillRepository
import digital.fischers.coinsaw.ui.utils.CreateUiStates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID
import javax.inject.Inject

class BillRepositoryImpl @Inject constructor(
    private val billDao: BillDao,
    private val changelogProcessor: ChangelogProcessor
) : BillRepository {
    override fun getAllBillsStream(): Flow<List<Bill>> {
        return billDao.getAllBills()
    }

    override fun getBillsByGroupStream(groupId: String): Flow<List<Bill>> {
        return billDao.getAllBillsByGroup(groupId)
    }

    override fun getBillsByGroupAndIsDeletedStream(
        groupId: String,
        isDeleted: Boolean
    ): Flow<List<Bill>> {
        return billDao.getAllBillsByGroupAndIsDeleted(groupId, isDeleted)
    }

    override fun getBillStream(billId: String): Flow<Bill?> {
        return billDao.getBillById(billId)
    }

    override suspend fun createBill(groupId: String, bill: CreateUiStates.Bill) {
        val entry = Entry(
            id = UUID.randomUUID().toString(),
            groupId = groupId,
            type = EntryType.BILL,
            action = EntryAction.CREATE,
            timestamp = System.currentTimeMillis(),
            payload = Payload.Bill(
                id = UUID.randomUUID().toString(),
                name = bill.name,
                amount = bill.amount.toDouble(),
                isDeleted = false,
                payerId = bill.payerId,
                participants = bill.splitting.map {
                    Payload.Participant(
                        userId = it.userId,
                        percentage = it.percentage.toDouble() / 100
                    )
                }
            )
        )

        changelogProcessor.processEntry(entry, false)
    }

    override suspend fun updateBill(groupId: String, changes: Payload.Bill) {
        val entry = Entry(
            id = UUID.randomUUID().toString(),
            groupId = groupId,
            type = EntryType.BILL,
            action = EntryAction.UPDATE,
            timestamp = System.currentTimeMillis(),
            payload = changes
        )

        changelogProcessor.processEntry(entry, false)
    }

    override suspend fun deleteBill(billId: String) {
        val bill = billDao.getBillById(billId).firstOrNull()
        if(bill != null) {
            val entry = Entry(
                id = UUID.randomUUID().toString(),
                groupId = bill.groupId,
                type = EntryType.BILL,
                action = EntryAction.UPDATE,
                timestamp = System.currentTimeMillis(),
                payload = Payload.Bill(
                    id = bill.id,
                    isDeleted = true
                )
            )

            changelogProcessor.processEntry(entry, false)
        }
    }
}