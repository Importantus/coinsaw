package digital.fischers.coinsaw.data.remote

data class CreateSessionRequest(
    val token: String
)

data class CreateSessionResponse(
    val admin: Boolean,
    val creation_timestamp: String,
    val last_active_timestamp: String,
    val id: String,
    val token: String
)

data class Session(
    val admin: Boolean,
    val creation_timestamp: String,
    val last_active_timestamp: String,
    val id: String,
)