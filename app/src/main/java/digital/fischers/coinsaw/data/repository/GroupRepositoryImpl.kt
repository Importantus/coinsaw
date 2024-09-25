package digital.fischers.coinsaw.data.repository

import android.util.Log
import digital.fischers.coinsaw.data.database.BillDao
import digital.fischers.coinsaw.data.database.CalculatedTransactionDao
import digital.fischers.coinsaw.data.database.ChangelogDao
import digital.fischers.coinsaw.data.database.Group
import digital.fischers.coinsaw.data.database.GroupDao
import digital.fischers.coinsaw.data.database.UserDao
import digital.fischers.coinsaw.domain.changelog.ChangelogProcessor
import digital.fischers.coinsaw.domain.changelog.Entry
import digital.fischers.coinsaw.domain.changelog.EntryAction
import digital.fischers.coinsaw.domain.changelog.EntryType
import digital.fischers.coinsaw.domain.changelog.Payload
import digital.fischers.coinsaw.domain.repository.CalculatedTransactionRepository
import digital.fischers.coinsaw.domain.repository.GroupRepository
import digital.fischers.coinsaw.ui.utils.CreateUiStates
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val groupDao: GroupDao,
    private val userDao: UserDao,
    private val billDao: BillDao,
    private val changelogDao: ChangelogDao,
    private val calculatedTransactionDao: CalculatedTransactionDao,
    private val calculatedTransactionRepository: CalculatedTransactionRepository,
    private val changelogProcessor: ChangelogProcessor
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

    override suspend fun setGroupOpen(groupId: String, open: Boolean) {
        if (open)
            groupDao.setGroupLastOpened(groupId, System.currentTimeMillis())
        Log.d("GroupRepositoryImpl", "Setting group $groupId open to $open")
        groupDao.setGroupOpen(groupId, open)
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

        changelogProcessor.processEntry(entry, false)
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
            apiEndpoint = null,
            sessionId = null
        )

        changelogProcessor.processEntry(
            Entry(
                id = UUID.randomUUID().toString(),
                groupId = newGroup.id,
                type = EntryType.SETTINGS,
                action = EntryAction.CREATE,
                timestamp = System.currentTimeMillis(),
                payload = Payload.GroupSettings(
                    name = newGroup.name,
                    currency = newGroup.currency
                )
            ), false
        )

        return newGroup
    }

    override suspend fun deleteGroup(groupId: String) {
        changelogDao.deleteAllByGroupId(groupId)
        billDao.deleteAllByGroupId(groupId)
        userDao.deleteAllByGroupId(groupId)
        calculatedTransactionDao.deleteAllByGroupId(groupId)
        return groupDao.delete(groupId)
    }

    override suspend fun syncGroup(groupId: String) {
        TODO("Not yet implemented")
    }

}