package digital.fischers.coinsaw.data.remote

data class CreateSessionRequest(
    val token: String,
    val name: String
)

data class CreateSessionResponse(
    val admin: Boolean,
    val creation_timestamp: String,
    val last_active_timestamp: String,
    val id: String,
    val token: String,
    val group: GroupWithoutToken,
    val name: String
)

data class Session(
    val name: String,
    val admin: Boolean,
    val creation_timestamp: String,
    val last_active_timestamp: String,
    val id: String,
)