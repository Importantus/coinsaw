package digital.fischers.coinsaw.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import digital.fischers.coinsaw.domain.repository.ChangelogRepository
import digital.fischers.coinsaw.domain.repository.GroupRepository
import digital.fischers.coinsaw.domain.repository.UserRepository
import digital.fischers.coinsaw.notifications.NotificationHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NotificationModule {
    @Provides
    @Singleton
    fun provideNotificationHelper(
        @ApplicationContext context: Context,
        groupRepository: GroupRepository,
        userRepository: UserRepository,
        changelogRepository: ChangelogRepository
    ): NotificationHelper {
        return NotificationHelper(
            context = context,
            groupRepository = groupRepository,
            userRepository = userRepository,
            changelogRepository = changelogRepository
        )
    }
}