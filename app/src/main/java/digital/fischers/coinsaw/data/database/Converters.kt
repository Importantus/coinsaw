package digital.fischers.coinsaw.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromSplittingList(splittings: List<Splitting>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Splitting>>() {}.type
        return gson.toJson(splittings, type)
    }

    @TypeConverter
    fun toSplittingList(splittingString: String): List<Splitting> {
        val gson = Gson()
        val type = object : TypeToken<List<Splitting>>() {}.type
        return gson.fromJson(splittingString, type)
    }
}