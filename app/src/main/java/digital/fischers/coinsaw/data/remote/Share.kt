package digital.fischers.coinsaw.data.remote

data class CreateShareRequest(
    val admin: Boolean,
    val maxSessions: Int,
)

data class CreateShareResponse(
    val id: String,
    val admin: Boolean,
    val maxSessions: Int,
    val active: Boolean,
    val token: String,
)

data class Share(
    val id: String,
    val admin: Boolean,
    val maxSessions: Int,
    val active: Boolean,
    val sessions: List<Session>,
)

data class ShareWithToken(
    val id: String,
    val admin: Boolean,
    val maxSessions: Int,
    val active: Boolean,
    val token: String,
    val sessions: List<Session>,
)