package digital.fischers.coinsaw.data.repository

import android.util.Log
import androidx.compose.ui.util.fastMap
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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
import digital.fischers.coinsaw.data.util.ChangelogJSONAdapter
import digital.fischers.coinsaw.data.util.decodeToken
import digital.fischers.coinsaw.domain.changelog.Entry
import digital.fischers.coinsaw.domain.repository.GroupRepository
import digital.fischers.coinsaw.domain.repository.RemoteRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONObject
import retrofit2.Response
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

    override suspend fun createGroup(
        groupId: String,
        serverUrl: String
    ): Response<CreateGroupResponse> {
        val groupResponse = apiService.createGroup(
            appendToServerUrl(serverUrl, ApiPath.CREATE_GROUP),
            CreateGroupRequest(groupId)
        )
        val localGroup = groupDao.getGroup(groupId).firstOrNull()
            ?: throw IllegalStateException("Group not found")
        groupDao.update(localGroup.copy(online = true, apiEndpoint = serverUrl))
        return groupResponse
    }

    override suspend fun deleteGroup(groupId: String): Response<Unit> {
        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(groupId)
        return apiService.deleteGroup(
            appendToServerUrl(serverUrl, ApiPath.DELETE_GROUP),
            accessToken,
            groupId
        )
    }

    override suspend fun syncGroup(groupId: String): Response<Unit> {
        val group = groupDao.getGroup(groupId).firstOrNull()
            ?: throw IllegalStateException("Group not found")

        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(group)

        val lastSyncTimestamp = group.lastSync ?: 0

        val localChanges =
            (changelogDao.getEntriesByGroupYoungerThanTimestamp(groupId, lastSyncTimestamp)
                .firstOrNull()
                ?: emptyList()).fastMap {
                GsonBuilder().registerTypeAdapter(Entry::class.java, ChangelogJSONAdapter())
                    .create().fromJson(it.content, Entry::class.java)
            }

        val postResponse = apiService.postChangelog(
            appendToServerUrl(serverUrl, ApiPath.POST_ENTRIES),
            accessToken,
            localChanges
        )

        if (postResponse.isSuccessful) {
            val getResponse = apiService.getChangelog(
                appendToServerUrl(serverUrl, ApiPath.GET_ENTRIES),
                accessToken,
                lastSyncTimestamp
            )

            if (getResponse.isSuccessful) {
                val remoteChanges = getResponse.body()!!

                remoteChanges.forEach {
                    groupRepository.processEntry(it)
                }

                val groupWithLastChanges = groupDao.getGroup(groupId).firstOrNull()
                    ?: throw IllegalStateException("Group not found")
                groupDao.update(groupWithLastChanges.copy(lastSync = System.currentTimeMillis()))
            } else {
                return Response.error(getResponse.code(), getResponse.errorBody()!!)
            }
        } else {
            return postResponse
        }

        return postResponse
    }

    override suspend fun createShare(
        groupId: String,
        options: CreateShareRequest
    ): Response<CreateShareResponse> {
        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(groupId)
        return apiService.createShare(
            appendToServerUrl(serverUrl, ApiPath.CREATE_SHARE),
            accessToken,
            options
        )
    }

    override suspend fun getAllShares(groupId: String): Response<List<Share>> {
        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(groupId)
        return apiService.getAllShares(
            appendToServerUrl(serverUrl, ApiPath.GET_ALL_SHARES),
            accessToken
        )
    }

    override suspend fun getShare(groupId: String, shareId: String): Response<ShareWithToken> {
        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(groupId)
        return apiService.getShare(
            appendToServerUrl(serverUrl, ApiPath.GET_SHARE) + "/${shareId}",
            accessToken
        )
    }

    override suspend fun deleteShare(groupId: String, shareId: String): Response<Unit> {
        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(groupId)
        return apiService.deleteShare(
            appendToServerUrl(serverUrl, ApiPath.DELETE_SHARE),
            accessToken,
            shareId
        )
    }

    override suspend fun createSession(
        options: CreateSessionRequest
    ): Response<CreateSessionResponse> {
        try {
            val decodedToken = JSONObject(decodeToken(options.token))
            val serverUrl = decodedToken.getString("server")
            val groupId = decodedToken.getString("groupId")
            val response = apiService.createSession(
                appendToServerUrl(serverUrl, ApiPath.CREATE_SESSION),
                options
            )

            if (response.isSuccessful) {
                val group = groupDao.getGroup(groupId).firstOrNull()

                val session = response.body()!!

                if (group == null) {
                    groupDao.insert(
                        Group(
                            id = groupId,
                            name = "",
                            currency = "",
                            online = true,
                            createdAt = System.currentTimeMillis(),
                            lastSync = null,
                            admin = session.admin,
                            accessToken = session.token,
                            apiEndpoint = serverUrl,
                            sessionId = session.id
                        )
                    )
                } else {
                    groupDao.update(
                        group.copy(
                            sessionId = session.id,
                            accessToken = session.token,
                            admin = session.admin,
                            online = true
                        )
                    )
                }
            }
            return response
        } catch (e: Exception) {
            Log.e("RemoteRepositoryImpl", "Error creating session", e)
            return Response.error(400, null)
        }


    }

    override suspend fun getAllSessions(groupId: String): Response<List<Session>> {
        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(groupId)
        return apiService.getAllSessions(
            appendToServerUrl(serverUrl, ApiPath.GET_ALL_SESSIONS),
            accessToken
        )
    }

    override suspend fun deleteSession(groupId: String, sessionId: String): Response<Unit> {
        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(groupId)
        return apiService.deleteSession(
            appendToServerUrl(serverUrl, ApiPath.DELETE_SESSION),
            accessToken,
            sessionId
        )
    }
}