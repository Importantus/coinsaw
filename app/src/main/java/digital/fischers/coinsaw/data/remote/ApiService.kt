package digital.fischers.coinsaw.data.remote

import digital.fischers.coinsaw.data.database.Changelog
import digital.fischers.coinsaw.domain.changelog.Entry
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @POST
    suspend fun createGroup(@Url url: String, @Body body: CreateGroupRequest): CreateGroupResponse

    @DELETE
    suspend fun deleteGroup(
        @Url url: String,
        @Header("Authorization") adminSessionToken: String,
        @Path("id") id: String
    )

    @POST
    suspend fun createSession(@Url url: String, @Body body: CreateSessionRequest): CreateSessionResponse

    @GET
    suspend fun getAllSessions(@Url url: String, @Header("Authorization") adminSessionToken: String): List<Session>

    @DELETE
    suspend fun deleteSession(
        @Url url: String,
        @Header("Authorization") adminSessionToken: String,
        @Path("id") id: String
    )

    @POST
    suspend fun createShare(
        @Url url: String,
        @Header("Authorization") adminSessionToken: String,
        @Body body: CreateShareRequest
    ): CreateShareResponse

    @GET
    suspend fun getAllShares(@Url url: String, @Header("Authorization") adminSessionToken: String): List<Share>

    @GET
    suspend fun getShare(
        @Url url: String,
        @Header("Authorization") adminSessionToken: String
    ): ShareWithToken

    @DELETE
    suspend fun deleteShare(
        @Url url: String,
        @Header("Authorization") adminSessionToken: String,
        @Path("id") id: String
    )

    @POST
    suspend fun postChangelog(
        @Url url: String,
        @Header("Authorization") privateSessionToken: String,
        @Body body: List<Entry>
    )

    @GET
    suspend fun getChangelog(
        @Url url: String,
        @Header("Authorization") privateSessionToken: String,
        @Query("from") from: Long,
        @Query("to") to: Long? = null
    ): List<Entry>
}