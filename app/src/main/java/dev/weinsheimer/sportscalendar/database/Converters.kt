package dev.weinsheimer.sportscalendar.database

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class Converters {
    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("yyyy-MM-dd")

    @TypeConverter
    fun stringToDate(value: String?): Date? {
        return value?.let { sdf.parse(it) }
    }

    @TypeConverter
    fun dateToString(date: Date?): String? {
        return sdf.format(date)
    }

    @TypeConverter
    fun intListToString(value: List<Int>?): String? {
        return value?.let { Gson().toJson(value) }
    }

    @TypeConverter
    fun stringToIntList(value: String?): List<Int>? {
        return Gson().fromJson(value, object : TypeToken<List<Int>>() { }.type)
    }
}