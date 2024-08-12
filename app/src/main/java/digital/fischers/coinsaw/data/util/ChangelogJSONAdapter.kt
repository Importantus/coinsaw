package digital.fischers.coinsaw.data.util

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import digital.fischers.coinsaw.domain.changelog.Entry
import digital.fischers.coinsaw.domain.changelog.EntryAction
import digital.fischers.coinsaw.domain.changelog.EntryType
import digital.fischers.coinsaw.domain.changelog.Payload
import java.lang.reflect.Type

class ChangelogJSONAdapter: JsonSerializer<Entry>, JsonDeserializer<Entry> {
    override fun serialize(
        src: Entry?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        // Make JsonElement from Entry
        return Gson().toJsonTree(src)
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Entry {
        val jsonObject = json?.asJsonObject
        val payload = EntryType.valueOf(jsonObject?.get("type")?.asString ?: "").let { type ->
            jsonObject?.get("payload")?.asJsonObject?.let {
                when (type) {
                    EntryType.SETTINGS -> Gson().fromJson(it, Payload.GroupSettings::class.java)
                    EntryType.USER -> Gson().fromJson(it, Payload.User::class.java)
                    EntryType.BILL -> Gson().fromJson(it, Payload.Bill::class.java)
                }
            }
        }
        return Entry(
            id = jsonObject?.get("id")?.asString ?: "",
            groupId = jsonObject?.get("groupId")?.asString ?: "",
            type = EntryType.valueOf(jsonObject?.get("type")?.asString ?: ""),
            action = EntryAction.valueOf(jsonObject?.get("action")?.asString ?: ""),
            timestamp = jsonObject?.get("timestamp")?.asLong ?: 0,
            syncTimestamp = jsonObject?.get("syncTimestamp")?.asLong,
            payload = payload ?: throw IllegalStateException("Payload not found")
        )
    }
}