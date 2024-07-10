package digital.fischers.coinsaw.domain.repository

import digital.fischers.coinsaw.data.database.User
import digital.fischers.coinsaw.domain.changelog.Payload
import digital.fischers.coinsaw.ui.utils.CreateUiStates
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getAllUsersByGroupIdStream(groupId: String): Flow<List<User>>
    fun getUsersByGroupIdAndIsDeletedStream(groupId: String, isDeleted: Boolean): Flow<List<User>>
    fun getUserByGroupIdAndIsMeStream(groupId: String, isMe: Boolean): Flow<User?>
    fun getUserStream(userId: String): Flow<User?>

    suspend fun createUser(groupId: String, user: CreateUiStates.User)
    suspend fun updateUser(groupId: String, changes: Payload.User)

    suspend fun setUserAsMe(userId: String)
}