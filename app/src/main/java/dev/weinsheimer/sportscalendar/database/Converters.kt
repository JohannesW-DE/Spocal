package dev.weinsheimer.sportscalendar.database

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Contains methods annotated with @TypeConverter used by Room to be able to store Date objects.
 */
class Converters {
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    @TypeConverter
    fun dateToString(date: Date?): String? {
        return sdf.format(date)
    }

    @TypeConverter
    fun stringToDate(value: String?): Date? {
        return value?.let { sdf.parse(it) }
    }
}