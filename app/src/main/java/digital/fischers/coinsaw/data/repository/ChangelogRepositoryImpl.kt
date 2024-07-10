package digital.fischers.coinsaw.data.repository

import com.google.gson.Gson
import digital.fischers.coinsaw.data.database.Changelog
import digital.fischers.coinsaw.data.database.ChangelogDao
import digital.fischers.coinsaw.domain.changelog.Entry
import digital.fischers.coinsaw.domain.repository.ChangelogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChangelogRepositoryImpl @Inject constructor(
    private val changelogDao: ChangelogDao
) : ChangelogRepository {
    override fun getChangelogById(id: String): Flow<Changelog> {
        return changelogDao.getEntry(id)
    }

    override fun getChangelogByGroupStream(groupId: String): Flow<List<Changelog>> {
        return changelogDao.getEntriesByGroup(groupId)
    }

    override fun getChangelogByGroupYoungerThanTimestampStream(
        groupId: String,
        timestamp: Long
    ): Flow<List<Changelog>> {
        return changelogDao.getEntriesByGroupYoungerThanTimestamp(groupId, timestamp)
    }

    override fun getChangelogByGroupOlderThanTimestampStream(
        groupId: String,
        timestamp: Long
    ): Flow<List<Changelog>> {
        return changelogDao.getEntriesByGroupOlderThanTimestamp(groupId, timestamp)
    }

    override suspend fun insert(entry: Entry) {
        val entryJson = Gson().toJson(entry)
        changelogDao.insert(
            Changelog(
                id = entry.id,
                timestamp = entry.timestamp,
                groupId = entry.groupId,
                synced = entry.syncTimestamp != null,
                content = entryJson
            )
        )
    }
}