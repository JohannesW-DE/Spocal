package dev.weinsheimer.sportscalendar.database.dao

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
