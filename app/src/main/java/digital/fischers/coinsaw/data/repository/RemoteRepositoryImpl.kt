package digital.fischers.coinsaw.data.repository

import androidx.compose.ui.util.fastMap
import com.google.gson.Gson
import com.google.gson.JsonObject
import digital.fischers.coinsaw.data.database.ChangelogDao
import digital.fischers.coinsaw.data.database.Group
import digital.fischers.coinsaw.data.database.GroupDao
import digital.fischers.coinsaw.data.remote.ApiPath
import digital.fischers.coinsaw.data.remote.ApiService
import digital.fischers.coinsaw.data.remote.CreateGroupRequest
import digital.fischers.coinsaw.data.remote.CreateGroupResponse
import digital.fischers.coinsaw.data.remote.CreateSessionRequest
import digital.fischers.coinsaw.data.remote.CreateSessionResponse
import digital.fischers.coinsaw.data.remote.CreateShareRequest
import digital.fischers.coinsaw.data.remote.CreateShareResponse
import digital.fischers.coinsaw.data.remote.Session
import digital.fischers.coinsaw.data.remote.Share
import digital.fischers.coinsaw.data.remote.ShareWithToken
import digital.fischers.coinsaw.data.util.decodeToken
import digital.fischers.coinsaw.domain.changelog.Entry
import digital.fischers.coinsaw.domain.repository.GroupRepository
import digital.fischers.coinsaw.domain.repository.RemoteRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONObject
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val groupDao: GroupDao,
    private val changelogDao: ChangelogDao,
    private val groupRepository: GroupRepository
) : RemoteRepository {
    private suspend fun getAccessTokenAndServerUrl(groupId: String): Pair<String, String> {
        val group = groupDao.getGroup(groupId).firstOrNull()
            ?: throw IllegalStateException("Group not found")
        return getAccessTokenAndServerUrl(group)
    }

    private fun getAccessTokenAndServerUrl(group: Group): Pair<String, String> {
        return Pair(
            ("Bearer " + (group.accessToken
                ?: throw IllegalStateException("Access token not found"))),
            group.apiEndpoint ?: throw IllegalStateException("API endpoint not found")
        )
    }

    private fun appendToServerUrl(serverUrl: String, path: ApiPath): String {
        return "${serverUrl.removeSuffix("/")}/${path.path.removePrefix("/")}"
    }

    override suspend fun createGroup(groupId: String, serverUrl: String): CreateGroupResponse {
        val groupResponse = apiService.createGroup(
            appendToServerUrl(serverUrl, ApiPath.CREATE_GROUP),
            CreateGroupRequest(groupId)
        )
        val localGroup = groupDao.getGroup(groupId).firstOrNull()
            ?: throw IllegalStateException("Group not found")
        groupDao.update(localGroup.copy(online = true, apiEndpoint = serverUrl))
        return groupResponse
    }

    override suspend fun deleteGroup(groupId: String) {
        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(groupId)
        return apiService.deleteGroup(appendToServerUrl(serverUrl, ApiPath.DELETE_GROUP), accessToken, groupId)
    }

    override suspend fun syncGroup(groupId: String) {
        val group = groupDao.getGroup(groupId).firstOrNull()
            ?: throw IllegalStateException("Group not found")

        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(group)

        val lastSyncTimestamp = group.lastSync ?: 0

        val localChanges = (changelogDao.getEntriesByGroupYoungerThanTimestamp(groupId, lastSyncTimestamp).firstOrNull()
            ?: emptyList()).fastMap {
                Gson().fromJson(it.content, Entry::class.java)
            }

        apiService.postChangelog(appendToServerUrl(serverUrl, ApiPath.POST_ENTRIES), accessToken, localChanges)

        val remoteChanges = apiService.getChangelog(appendToServerUrl(serverUrl, ApiPath.GET_ENTRIES), accessToken, lastSyncTimestamp)
        remoteChanges.forEach {
             groupRepository.processEntry(it)
        }
    }

    override suspend fun createShare(
        groupId: String,
        options: CreateShareRequest
    ): CreateShareResponse {
        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(groupId)
        return apiService.createShare(
            appendToServerUrl(serverUrl, ApiPath.CREATE_SHARE),
            accessToken,
            options
        )
    }

    override suspend fun getAllShares(groupId: String): List<Share> {
        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(groupId)
        return apiService.getAllShares(appendToServerUrl(serverUrl, ApiPath.GET_ALL_SHARES), accessToken)
    }

    override suspend fun getShare(groupId: String, shareId: String): ShareWithToken {
        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(groupId)
        return apiService.getShare(appendToServerUrl(serverUrl, ApiPath.GET_SHARE), accessToken, shareId)
    }

    override suspend fun deleteShare(groupId: String, shareId: String) {
        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(groupId)
        return apiService.deleteShare(appendToServerUrl(serverUrl, ApiPath.DELETE_SHARE), accessToken, shareId)
    }

    override suspend fun createSession(
        options: CreateSessionRequest
    ) {
        val decodedToken = JSONObject(decodeToken(options.token))
        val serverUrl = decodedToken.getString("server")
        val groupId = decodedToken.getString("groupId")
        val response = apiService.createSession(
            appendToServerUrl(serverUrl, ApiPath.CREATE_SESSION),
            options
        )
        groupDao.update(groupDao.getGroup(groupId).first().copy(
            sessionId = response.id,
            accessToken = response.token,
            admin = response.admin,
            online = true
        ))
    }

    override suspend fun getAllSessions(groupId: String): List<Session> {
        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(groupId)
        return apiService.getAllSessions(appendToServerUrl(serverUrl, ApiPath.GET_ALL_SESSIONS), accessToken)
    }

    override suspend fun deleteSession(groupId: String, sessionId: String) {
        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(groupId)
        return apiService.deleteSession(appendToServerUrl(serverUrl, ApiPath.DELETE_SESSION), accessToken, sessionId)
    }
}