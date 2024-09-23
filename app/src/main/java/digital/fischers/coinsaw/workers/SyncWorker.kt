package digital.fischers.coinsaw.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import digital.fischers.coinsaw.domain.repository.RemoteRepository

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val remoteRepository: RemoteRepository
) : CoroutineWorker(
    appContext, workerParams
) {
    companion object {
        const val WORK_NAME = "SyncWorker"
    }

    override suspend fun doWork(): Result {
        try {
            remoteRepository.syncAllGroups()
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}