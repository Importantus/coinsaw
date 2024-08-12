package digital.fischers.coinsaw.data.remote

data class CreateGroupRequest(
    val id: String
)

data class CreateGroupResponse(
    val id: String,
    val recoveryToken: String
)

data class GroupWithoutToken(
    val id: String
)