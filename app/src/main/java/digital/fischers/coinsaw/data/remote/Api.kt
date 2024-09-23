package digital.fischers.coinsaw.data.remote

import digital.fischers.coinsaw.domain.changelog.Entry
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface Api {
    @POST
    suspend fun createGroup(@Url url: String, @Body body: CreateGroupRequest): Response<CreateGroupResponse>

    @DELETE
    suspend fun deleteGroup(
        @Url url: String,
        @Header("Authorization") adminSessionToken: String,
        @Path("id") id: String
    ): Response<Unit>

    @POST
    suspend fun createSession(@Url url: String, @Body body: CreateSessionRequest): Response<CreateSessionResponse>

    @GET
    suspend fun getAllSessions(@Url url: String, @Header("Authorization") adminSessionToken: String): Response<List<Session>>

    @DELETE
    suspend fun deleteSession(
        @Url url: String,
        @Header("Authorization") adminSessionToken: String
    ): Response<Unit>

    @POST
    suspend fun createShare(
        @Url url: String,
        @Header("Authorization") adminSessionToken: String,
        @Body body: CreateShareRequest
    ): Response<CreateShareResponse>

    @GET
    suspend fun getAllShares(@Url url: String, @Header("Authorization") adminSessionToken: String): Response<List<Share>>

    @GET
    suspend fun getShare(
        @Url url: String,
        @Header("Authorization") adminSessionToken: String
    ): Response<ShareWithToken>

    @DELETE
    suspend fun deleteShare(
        @Url url: String,
        @Header("Authorization") adminSessionToken: String
    ): Response<Unit>

    @POST
    suspend fun postChangelog(
        @Url url: String,
        @Header("Authorization") privateSessionToken: String,
        @Body body: List<Entry>
    ): Response<Unit>

    @GET
    suspend fun getChangelog(
        @Url url: String,
        @Header("Authorization") privateSessionToken: String,
        @Query("from") from: Long,
        @Query("to") to: Long? = null
    ): Response<List<Entry>>

    @POST
    suspend fun syncChangelog(
        @Url url: String,
        @Header("Authorization") privateSessionToken: String,
        @Body body: SyncChangelogRequest
    ): Response<List<Entry>>
}