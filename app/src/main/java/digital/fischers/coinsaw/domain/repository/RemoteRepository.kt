package digital.fischers.coinsaw.domain.repository

import digital.fischers.coinsaw.data.remote.APIResult
import digital.fischers.coinsaw.data.remote.CreateGroupResponse
import digital.fischers.coinsaw.data.remote.CreateSessionRequest
import digital.fischers.coinsaw.data.remote.CreateSessionResponse
import digital.fischers.coinsaw.data.remote.CreateShareRequest
import digital.fischers.coinsaw.data.remote.CreateShareResponse
import digital.fischers.coinsaw.data.remote.Session
import digital.fischers.coinsaw.data.remote.Share
import digital.fischers.coinsaw.data.remote.ShareWithToken
import retrofit2.Response

interface RemoteRepository {
    suspend fun createGroup(groupId: String, serverUrl: String): APIResult<CreateGroupResponse>

    suspend fun deleteGroup(groupId: String): APIResult<Unit>

    suspend fun syncGroup(groupId: String): APIResult<Unit>

    suspend fun syncAllGroups()

    suspend fun createShare(groupId: String, options: CreateShareRequest): APIResult<CreateShareResponse>

    suspend fun getAllShares(groupId: String): APIResult<List<Share>>

    suspend fun getShare(groupId: String, shareId: String): APIResult<ShareWithToken>

    suspend fun deleteShare(groupId: String, shareId: String): APIResult<Unit>

    suspend fun createSession(options: CreateSessionRequest): APIResult<CreateSessionResponse>

    suspend fun getAllSessions(groupId: String): APIResult<List<Session>>

    suspend fun deleteSession(groupId: String, sessionId: String): APIResult<Unit>
}