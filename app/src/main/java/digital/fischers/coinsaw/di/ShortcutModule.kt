package digital.fischers.coinsaw.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import digital.fischers.coinsaw.domain.repository.GroupRepository
import digital.fischers.coinsaw.shortcuts.ShortcutHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ShortcutModule {
    @Provides
    @Singleton
    fun provideShortcutHelper(
        @ApplicationContext context: Context,
        groupRepository: GroupRepository
    ) = ShortcutHelper(context, groupRepository)
}