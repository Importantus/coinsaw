package digital.fischers.coinsaw.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import digital.fischers.coinsaw.data.database.CoinsawDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CoinsawDatabase {
        return Room.databaseBuilder(
            context,
            CoinsawDatabase::class.java,
            "coinsaw-database"
        ).build()
    }

    @Provides
    fun provideGroupDao(database: CoinsawDatabase) = database.groupDao()

    @Provides
    fun provideBillDao(database: CoinsawDatabase) = database.billDao()

    @Provides
    fun provideUserDao(database: CoinsawDatabase) = database.userDao()

    @Provides
    fun provideCalculatedTransactionDao(database: CoinsawDatabase) = database.calculatedTransactionDao()

    @Provides
    fun provideChangelogDao(database: CoinsawDatabase) = database.changelogDao()
}