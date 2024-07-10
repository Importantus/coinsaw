package digital.fischers.coinsaw.domain.changelog

enum class EntryType {
    SETTINGS,
    USER,
    BILL
}

enum class EntryAction {
    CREATE,UPDATE
}

data class Entry(
    val id: String,
    val groupId: String,
    val type: EntryType,
    val action: EntryAction,
    val timestamp: Long,
    val syncTimestamp: Long? = null,
    val payload: Payload
)

sealed class Payload {
    data class GroupSettings(
        val currency: String? = null,
        val name: String? = null
    ) : Payload()

    data class User(
        val id: String? = null,
        val name: String? = null,
        val isDeleted: Boolean? = null
    ) : Payload()

    data class Bill(
        val id: String? = null,
        val name: String? = null,
        val amount: Double? = null,
        val isDeleted: Boolean? = null,
        val payerId: String? = null,
        val participants: List<Participant>? = null
    ) : Payload()

    data class Participant(
        val userId: String,
        val percentage: Double
    )
}
