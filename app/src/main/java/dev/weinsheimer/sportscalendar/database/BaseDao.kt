package dev.weinsheimer.sportscalendar.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

interface BaseDao {
    fun resetAthleteFilters()
    fun updateAthleteFilter(id: Int)
    fun resetEventFilters()
    fun updateEventFilter(id: Int)
    fun resetEventCategoryFilters()
    fun updateEventCategoryFilter(id: Int)
    fun resetEventListStatus()
    fun changeEventListStatus(status: Boolean, id: Int)
    fun clearEntries()
}
