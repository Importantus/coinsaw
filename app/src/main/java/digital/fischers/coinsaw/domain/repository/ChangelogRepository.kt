package digital.fischers.coinsaw.domain.repository

import digital.fischers.coinsaw.data.database.Changelog
import digital.fischers.coinsaw.domain.changelog.Entry
import kotlinx.coroutines.flow.Flow

interface ChangelogRepository {
    fun getChangelogById(id: String): Flow<Changelog>
    fun getChangelogByGroupStream(groupId: String): Flow<List<Changelog>>
    fun getChangelogByGroupYoungerThanTimestampStream(groupId: String, timestamp: Long): Flow<List<Changelog>>
    fun getChangelogByGroupOlderThanTimestampStream(groupId: String, timestamp: Long): Flow<List<Changelog>>

    suspend fun insert(entry: Entry)
}