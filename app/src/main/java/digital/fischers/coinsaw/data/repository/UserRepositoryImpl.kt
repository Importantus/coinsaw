package digital.fischers.coinsaw.data.repository

import android.util.Log
import digital.fischers.coinsaw.data.database.User
import digital.fischers.coinsaw.data.database.UserDao
import digital.fischers.coinsaw.domain.changelog.Entry
import digital.fischers.coinsaw.domain.changelog.EntryAction
import digital.fischers.coinsaw.domain.changelog.EntryType
import digital.fischers.coinsaw.domain.changelog.Payload
import digital.fischers.coinsaw.domain.repository.GroupRepository
import digital.fischers.coinsaw.domain.repository.UserRepository
import digital.fischers.coinsaw.ui.utils.CreateUiStates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val groupRepository: GroupRepository
) : UserRepository {
    override fun getAllUsersByGroupIdStream(groupId: String): Flow<List<User>> {
        return userDao.getByGroupId(groupId)
    }

    override fun getUsersByGroupIdAndIsDeletedStream(
        groupId: String,
        isDeleted: Boolean
    ): Flow<List<User>> {
        return userDao.getByGroupIdAndIsDeleted(groupId, isDeleted)
    }

    override fun getUserByGroupIdAndIsMeStream(groupId: String, isMe: Boolean): Flow<User?> {
        return userDao.getByGroupIdAndIsMe(groupId, isMe)
    }

    override fun getUserStream(userId: String): Flow<User?> {
        return userDao.getById(userId)
    }

    override suspend fun createUser(groupId: String, user: CreateUiStates.User) {
        val userId = UUID.randomUUID().toString()

        val entry = Entry(
            id = UUID.randomUUID().toString(),
            groupId = groupId,
            type = EntryType.USER,
            action = EntryAction.CREATE,
            timestamp = System.currentTimeMillis(),
            payload = Payload.User(
                id = userId,
                name = user.name
            )
        )

        groupRepository.processEntry(entry)

        if(user.isMe) {
            setUserAsMe(groupId, userId, true)
        }

        return
    }

    override suspend fun updateUser(groupId: String, changes: Payload.User) {
        val entry = Entry(
            id = UUID.randomUUID().toString(),
            groupId = groupId,
            type = EntryType.USER,
            action = EntryAction.UPDATE,
            timestamp = System.currentTimeMillis(),
            payload = changes
        )

        groupRepository.processEntry(entry)
    }


    override suspend fun setUserAsMe(groupId: String, userId: String, isMeValue: Boolean) {
        // If user is set as "me", set all other users as not me
        if(isMeValue) {
            userDao.getByGroupId(groupId).firstOrNull()?.forEach {
                userDao.update(
                    it.copy(
                        isMe = false
                    )
                )
            }
        }

        userDao.getById(userId).firstOrNull()?.copy(
            isMe = isMeValue
        )?.let {
            userDao.update(
                it
            )
        }
    }
}