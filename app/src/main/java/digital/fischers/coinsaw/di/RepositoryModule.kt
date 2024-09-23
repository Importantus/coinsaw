package digital.fischers.coinsaw.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import digital.fischers.coinsaw.data.model.ChangelogProcessorImpl
import digital.fischers.coinsaw.data.repository.BillRepositoryImpl
import digital.fischers.coinsaw.data.repository.CalculatedTransactionRepositoryImpl
import digital.fischers.coinsaw.data.repository.ChangelogRepositoryImpl
import digital.fischers.coinsaw.data.repository.GroupRepositoryImpl
import digital.fischers.coinsaw.data.repository.RemoteRepositoryImpl
import digital.fischers.coinsaw.data.repository.UserRepositoryImpl
import digital.fischers.coinsaw.domain.changelog.ChangelogProcessor
import digital.fischers.coinsaw.domain.repository.BillRepository
import digital.fischers.coinsaw.domain.repository.CalculatedTransactionRepository
import digital.fischers.coinsaw.domain.repository.ChangelogRepository
import digital.fischers.coinsaw.domain.repository.GroupRepository
import digital.fischers.coinsaw.domain.repository.RemoteRepository
import digital.fischers.coinsaw.domain.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindChangelogProcessor(impl: ChangelogProcessorImpl): ChangelogProcessor

    @Binds
    @Singleton
    abstract fun bindGroupRepository(impl: GroupRepositoryImpl): GroupRepository

    @Binds
    @Singleton
    abstract fun bindBillRepository(impl: BillRepositoryImpl): BillRepository

    @Binds
    @Singleton
    abstract fun bindCalculatedTransactionRepository(impl: CalculatedTransactionRepositoryImpl): CalculatedTransactionRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindChangelogRepository(impl: ChangelogRepositoryImpl): ChangelogRepository

    @Binds
    @Singleton
    abstract fun bindRemoteRepository(impl: RemoteRepositoryImpl): RemoteRepository
}