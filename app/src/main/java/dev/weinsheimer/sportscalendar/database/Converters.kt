package dev.weinsheimer.sportscalendar.database

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class Converters {
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    @TypeConverter
    fun stringToDate(value: String?): Date? {
        return value?.let { sdf.parse(it) }
    }

    @TypeConverter
    fun dateToString(date: Date?): String? {
        return sdf.format(date)
    }
}