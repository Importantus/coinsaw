package digital.fischers.coinsaw.data.remote

import digital.fischers.coinsaw.domain.changelog.Entry

data class SyncChangelogRequest(
    val lastSync: Long,
    val data: List<Entry>
)