package dev.weinsheimer.sportscalendar.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.weinsheimer.sportscalendar.database.model.*

@Dao
interface CyclingDao : BaseDao {
    /**
     * athletes
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAthletes(vararg athletes: DatabaseCyclingAthlete)

    @Delete
    fun deleteAthletes(vararg athletes: DatabaseCyclingAthlete)

    @Update
    fun updateAthletes(vararg athletes: DatabaseCyclingAthlete)

    @Query("UPDATE cycling_athletes SET filter = 0")
    override fun resetAthleteFilters()

    @Query("UPDATE cycling_athletes SET filter = 1 WHERE id = :id")
    override fun updateAthleteFilter(id: Int)

    @Query("SELECT * FROM cycling_athletes")
    fun getCurrentAthletes(): List<DatabaseCyclingAthlete>

    @Query("SELECT * FROM cycling_athletes")
    fun getAthletes(): LiveData<List<DatabaseCyclingAthlete>>

    /**
     * events
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEvents(vararg events: DatabaseCyclingEvent)

    @Delete
    fun deleteEvents(vararg events: DatabaseCyclingEvent)

    @Update
    fun updateEvents(vararg events: DatabaseCyclingEvent)

    @Query("UPDATE cycling_events SET filter = 0")
    override fun resetEventFilters()

    @Query("UPDATE cycling_events SET filter = 1 WHERE id = :id")
    override fun updateEventFilter(id: Int)

    @Query("UPDATE cycling_events SET list = 0")
    override fun resetEventListStatus()

    @Query("UPDATE cycling_events SET list = :status WHERE id = :id")
    override fun changeEventListStatus(status: Boolean, id: Int)

    @Query("SELECT * FROM cycling_events")
    fun getCurrentEvents(): List<DatabaseCyclingEvent>

    @Query("SELECT * FROM cycling_events")
    fun getEvents(): LiveData<List<DatabaseCyclingEvent>>

    /**
     * event categories
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEventCategories(vararg categories: DatabaseCyclingEventCategory)

    @Delete
    fun deleteEventCategories(vararg categories: DatabaseCyclingEventCategory)

    @Update
    fun updateEventCategories(vararg categories: DatabaseCyclingEventCategory)

    @Query("UPDATE cycling_event_categories SET filter = 0")
    override fun resetEventCategoryFilters()

    @Query("UPDATE cycling_event_categories SET filter = 1 WHERE id = :id")
    override fun updateEventCategoryFilter(id: Int)

    @Query("SELECT * FROM cycling_event_categories")
    fun getCurrentEventCategories(): List<DatabaseCyclingEventCategory>

    @Query("SELECT * FROM cycling_event_categories")
    fun getEventCategories(): LiveData<List<DatabaseCyclingEventCategory>>

    @Transaction @Query("SELECT cycling_events.*, cycling_event_categories.id as category_id, cycling_event_categories.name as category_name, cycling_event_categories.main_category_id as category_main_category_id, cycling_event_categories.filter as category_filter, countries.id as country_id, countries.name as country_name, countries.alphatwo as country_alphatwo FROM cycling_events JOIN cycling_event_categories ON cycling_event_categories.id = cycling_events.category JOIN countries ON countries.id = cycling_events.country WHERE cycling_events.list = 1")
    fun getFilteredEvents(): LiveData<List<DatabaseCyclingEventWithAthletes>>

    /**
     * entries
     */
    @Query("UPDATE cycling_event_categories SET filter = 0 WHERE id = 99999")
    override fun clearEntries()
}