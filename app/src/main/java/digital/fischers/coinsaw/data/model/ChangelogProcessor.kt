package digital.fischers.coinsaw.data.model

import android.util.Log
import com.google.gson.Gson
import dagger.Lazy
import digital.fischers.coinsaw.data.database.Bill
import digital.fischers.coinsaw.data.database.BillDao
import digital.fischers.coinsaw.data.database.Changelog
import digital.fischers.coinsaw.data.database.ChangelogDao
import digital.fischers.coinsaw.data.database.Group
import digital.fischers.coinsaw.data.database.GroupDao
import digital.fischers.coinsaw.data.database.Splitting
import digital.fischers.coinsaw.data.database.User
import digital.fischers.coinsaw.data.database.UserDao
import digital.fischers.coinsaw.domain.changelog.ChangelogProcessor
import digital.fischers.coinsaw.domain.changelog.Entry
import digital.fischers.coinsaw.domain.changelog.EntryAction
import digital.fischers.coinsaw.domain.changelog.EntryType
import digital.fischers.coinsaw.domain.changelog.Payload
import digital.fischers.coinsaw.domain.repository.CalculatedTransactionRepository
import digital.fischers.coinsaw.domain.repository.RemoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChangelogProcessorImpl @Inject constructor(
    private val groupDao: GroupDao,
    private val userDao: UserDao,
    private val billDao: BillDao,
    private val changelogDao: ChangelogDao,
    private val calculatedTransactionRepository: CalculatedTransactionRepository,
    private val remoteRepository: Lazy<RemoteRepository>
) : ChangelogProcessor {
    override suspend fun processEntry(entry: Entry, fromRemote: Boolean) {
        // If changelog with this ID already exists, do nothing
        if (changelogDao.getEntry(entry.id).firstOrNull() != null) {
            return
        }

        val groupId: String = entry.groupId

        // Write to changelog
        val entryJson = Gson().toJson(entry)
        changelogDao.insert(
            Changelog(
                id = entry.id,
                timestamp = entry.timestamp,
                groupId = entry.groupId,
                synced = fromRemote,
                content = entryJson
            )
        )

        Log.d("GroupRepositoryImpl", "Processing entry: $entryJson")

        // Write to database
        when (entry.type) {
            EntryType.SETTINGS -> {
                when (entry.action) {
                    EntryAction.UPDATE -> {
                        val settings = entry.payload as Payload.GroupSettings
                        val group = groupDao.getGroup(groupId).first()
                        groupDao.update(
                            group.copy(
                                name = settings.name ?: group.name,
                                currency = settings.currency ?: group.currency
                            )
                        )
                    }

                    EntryAction.CREATE -> {
                        val settings = entry.payload as Payload.GroupSettings
                        val group = groupDao.getGroup(groupId).firstOrNull()
                        if (group == null) {
                            groupDao.insert(
                                Group(
                                    id = groupId,
                                    name = settings.name ?: "",
                                    currency = settings.currency ?: "",
                                    online = false,
                                    createdAt = entry.timestamp,
                                    lastSync = null,
                                    admin = true,
                                    accessToken = null,
                                    apiEndpoint = null,
                                    sessionId = null
                                )
                            )
                        } else {
                            groupDao.update(
                                group.copy(
                                    name = settings.name ?: group.name,
                                    currency = settings.currency ?: group.currency
                                )
                            )
                        }
                    }
                }
            }

            EntryType.BILL -> {
                when (entry.action) {
                    EntryAction.CREATE -> {
                        val bill = entry.payload as Payload.Bill
                        billDao.insertBill(
                            Bill(
                                id = bill.id ?: "",
                                groupId = groupId,
                                name = bill.name ?: "",
                                amount = bill.amount ?: 0.0,
                                userId = bill.payerId ?: "",
                                isDeleted = bill.isDeleted ?: false,
                                createdAt = entry.timestamp,
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
                        val changes = entry.payload as Payload.Bill
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
                when (entry.action) {
                    EntryAction.CREATE -> {
                        val user = entry.payload as Payload.User
                        userDao.insert(
                            User(
                                id = user.id ?: "",
                                groupId = groupId,
                                name = user.name ?: "",
                                isDeleted = user.isDeleted ?: false,
                                createdAt = entry.timestamp,
                                isMe = false
                            )
                        )
                    }

                    EntryAction.UPDATE -> {
                        val changes = entry.payload as Payload.User
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

        val group = groupDao.getGroup(groupId).firstOrNull()

        if(!fromRemote && group?.online == true) {
            CoroutineScope(Dispatchers.IO).launch {
                remoteRepository.get().syncGroup(groupId)
            }
        }

    }
}