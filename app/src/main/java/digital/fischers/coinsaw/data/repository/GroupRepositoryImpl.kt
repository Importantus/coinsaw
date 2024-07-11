package digital.fischers.coinsaw.data.repository

import com.google.gson.Gson
import digital.fischers.coinsaw.data.database.Bill
import digital.fischers.coinsaw.data.database.BillDao
import digital.fischers.coinsaw.data.database.Changelog
import digital.fischers.coinsaw.data.database.ChangelogDao
import digital.fischers.coinsaw.data.database.Group
import digital.fischers.coinsaw.data.database.GroupDao
import digital.fischers.coinsaw.data.database.Splitting
import digital.fischers.coinsaw.data.database.User
import digital.fischers.coinsaw.data.database.UserDao
import digital.fischers.coinsaw.domain.changelog.Entry
import digital.fischers.coinsaw.domain.changelog.EntryAction
import digital.fischers.coinsaw.domain.changelog.EntryType
import digital.fischers.coinsaw.domain.changelog.Payload
import digital.fischers.coinsaw.domain.repository.CalculatedTransactionRepository
import digital.fischers.coinsaw.domain.repository.GroupRepository
import digital.fischers.coinsaw.ui.utils.CreateUiStates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.InvalidPropertiesFormatException
import java.util.UUID
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val groupDao: GroupDao,
    private val userDao: UserDao,
    private val billDao: BillDao,
    private val changelogDao: ChangelogDao,
    private val calculatedTransactionRepository: CalculatedTransactionRepository
) : GroupRepository {
    override fun getAllGroupsStream(): Flow<List<Group>> {
        return groupDao.getGroups()
    }

    override fun getOnlineGroupsStream(): Flow<List<Group>> {
        return groupDao.getOnlineGroups()
    }

    override fun getOfflineGroupsStream(): Flow<List<Group>> {
        return groupDao.getOfflineGroups()
    }

    override fun getGroupStream(groupId: String): Flow<Group?> {
        return groupDao.getGroup(groupId)
    }

    override suspend fun processEntry(changeEntry: Entry) {
        // If changelog with this ID already exists, do nothing
        if (changelogDao.getEntry(changeEntry.id).firstOrNull() != null) {
            return
        }

        val groupId: String = changeEntry.groupId

        // Write to changelog
        val entryJson = Gson().toJson(changeEntry)
        changelogDao.insert(
            Changelog(
                id = changeEntry.id,
                timestamp = changeEntry.timestamp,
                groupId = changeEntry.groupId,
                synced = changeEntry.syncTimestamp != null,
                content = entryJson
            )
        )

        // Write to database
        when (changeEntry.type) {
            EntryType.SETTINGS -> {
                when (changeEntry.action) {
                    EntryAction.UPDATE -> {
                        val settings = changeEntry.payload as Payload.GroupSettings
                        val group = groupDao.getGroup(groupId).first()
                        groupDao.update(
                            group.copy(
                                name = settings.name ?: group.name,
                                currency = settings.currency ?: group.currency
                            )
                        )
                    }

                    EntryAction.CREATE -> {
                        throw InvalidPropertiesFormatException("Cannot create group settings")
                    }
                }
            }

            EntryType.BILL -> {
                when (changeEntry.action) {
                    EntryAction.CREATE -> {
                        val bill = changeEntry.payload as Payload.Bill
                        billDao.insertBill(
                            Bill(
                                id = bill.id ?: "",
                                groupId = groupId,
                                name = bill.name ?: "",
                                amount = bill.amount ?: 0.0,
                                userId = bill.payerId ?: "",
                                isDeleted = bill.isDeleted ?: false,
                                createdAt = System.currentTimeMillis(),
                                splittings = bill.participants?.map {
                                    Splitting(
                                        userId = it.userId,
                                        billId = bill.id ?: "",
                                        percent = it.percentage
                                    )
                                } ?: emptyList()
                            ))
                    }

                    EntryAction.UPDATE -> {
                        val changes = changeEntry.payload as Payload.Bill
                        val bill = billDao.getBillById(changes.id ?: "").first()

                        billDao.updateBill(
                            bill.copy(
                                name = changes.name ?: bill.name,
                                amount = changes.amount ?: bill.amount,
                                userId = changes.payerId ?: bill.userId,
                                isDeleted = changes.isDeleted ?: bill.isDeleted,
                                splittings = changes.participants?.map {
                                    Splitting(
                                        userId = it.userId,
                                        billId = bill.id,
                                        percent = it.percentage
                                    )
                                } ?: bill.splittings
                            )
                        )
                    }
                }
            }

            EntryType.USER -> {
                when (changeEntry.action) {
                    EntryAction.CREATE -> {
                        val user = changeEntry.payload as Payload.User
                        userDao.insert(
                            User(
                                id = user.id ?: "",
                                groupId = groupId,
                                name = user.name ?: "",
                                isDeleted = user.isDeleted ?: false,
                                createdAt = System.currentTimeMillis(),
                                isMe = false
                            )
                        )
                    }

                    EntryAction.UPDATE -> {
                        val changes = changeEntry.payload as Payload.User
                        val user = userDao.getById(changes.id ?: "").first()
                        return userDao.update(
                            user.copy(
                                name = changes.name ?: user.name,
                                isDeleted = changes.isDeleted ?: user.isDeleted
                            )
                        )
                    }
                }
            }
        }

        // Calculate transactions
        calculatedTransactionRepository.calculateForGroup(groupId)

        // Sync group if needed
        val group = groupDao.getGroup(groupId).first()
        if (group.online && group.lastSync != null && changeEntry.syncTimestamp == null) {
            syncGroup(groupId)
        }

    }

    override suspend fun updateGroup(groupId: String, changes: Payload.GroupSettings) {
        val entry = Entry(
            id = UUID.randomUUID().toString(),
            groupId = groupId,
            type = EntryType.SETTINGS,
            action = EntryAction.UPDATE,
            timestamp = System.currentTimeMillis(),
            payload = changes
        )

        processEntry(entry)
    }

    override suspend fun createGroup(group: CreateUiStates.Group): Group {
        val newGroup = Group(
            id = UUID.randomUUID().toString(),
            name = group.name,
            currency = group.currency,
            online = false,
            createdAt = System.currentTimeMillis(),
            lastSync = null,
            admin = true,
            accessToken = null,
            apiEndpoint = null
        )

        groupDao.insert(newGroup)

        return newGroup
    }

    override suspend fun deleteGroup(groupId: String) {
        return groupDao.delete(groupId)
    }

    override suspend fun syncGroup(groupId: String) {
        TODO("Not yet implemented")
    }

}