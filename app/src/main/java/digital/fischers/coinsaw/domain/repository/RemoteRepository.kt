package digital.fischers.coinsaw.domain.repository

import digital.fischers.coinsaw.data.remote.CreateGroupResponse
import digital.fischers.coinsaw.data.remote.CreateSessionRequest
import digital.fischers.coinsaw.data.remote.CreateSessionResponse
import digital.fischers.coinsaw.data.remote.CreateShareRequest
import digital.fischers.coinsaw.data.remote.CreateShareResponse
import digital.fischers.coinsaw.data.remote.Session
import digital.fischers.coinsaw.data.remote.Share
import digital.fischers.coinsaw.data.remote.ShareWithToken

interface RemoteRepository {
    suspend fun createGroup(groupId: String, serverUrl: String): CreateGroupResponse

    suspend fun deleteGroup(groupId: String)

    suspend fun syncGroup(groupId: String)

    suspend fun createShare(groupId: String, options: CreateShareRequest): CreateShareResponse

    suspend fun getAllShares(groupId: String): List<Share>

    suspend fun getShare(groupId: String, shareId: String): ShareWithToken

    suspend fun deleteShare(groupId: String, shareId: String)

    suspend fun createSession(groupId: String, options: CreateSessionRequest)

    suspend fun getAllSessions(groupId: String): List<Session>

    suspend fun deleteSession(groupId: String, sessionId: String)
}