package digital.fischers.coinsaw.domain.changelog

interface ChangelogProcessor {
    suspend fun processEntry(entry: Entry, fromRemote: Boolean)
}