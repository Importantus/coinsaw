package digital.fischers.coinsaw.domain.repository

import android.icu.util.Currency
import digital.fischers.coinsaw.data.database.Group
import digital.fischers.coinsaw.domain.changelog.Entry
import digital.fischers.coinsaw.domain.changelog.Payload
import digital.fischers.coinsaw.ui.utils.CreateUiStates
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    fun getAllGroupsStream(): Flow<List<Group>>
    fun getOnlineGroupsStream(): Flow<List<Group>>
    fun getOfflineGroupsStream(): Flow<List<Group>>
    fun getGroupStream(groupId: String): Flow<Group?>

    suspend fun processEntry(changeEntry: Entry)

    suspend fun updateGroup(groupId: String, changes: Payload.GroupSettings)
    suspend fun createGroup(group: CreateUiStates.Group): Group

    suspend fun deleteGroup(groupId: String)
    suspend fun syncGroup(groupId: String)
}