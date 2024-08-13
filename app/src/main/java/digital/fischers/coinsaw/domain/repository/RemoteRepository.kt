package digital.fischers.coinsaw.domain.repository

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
    suspend fun createGroup(groupId: String, serverUrl: String): Response<CreateGroupResponse>

    suspend fun deleteGroup(groupId: String): Response<Unit>

    suspend fun syncGroup(groupId: String): Response<Unit>

    suspend fun createShare(groupId: String, options: CreateShareRequest): Response<CreateShareResponse>

    suspend fun getAllShares(groupId: String): Response<List<Share>>

    suspend fun getShare(groupId: String, shareId: String): Response<ShareWithToken>

    suspend fun deleteShare(groupId: String, shareId: String): Response<Unit>

    suspend fun createSession(options: CreateSessionRequest): Response<CreateSessionResponse>

    suspend fun getAllSessions(groupId: String): Response<List<Session>>

    suspend fun deleteSession(groupId: String, sessionId: String): Response<Unit>
}