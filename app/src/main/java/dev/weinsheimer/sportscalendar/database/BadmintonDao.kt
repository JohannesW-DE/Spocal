package dev.weinsheimer.sportscalendar.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BadmintonDao : BaseDao {
    /**
     * athletes
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAthletes(vararg athletes: DatabaseBadmintonAthlete)

    @Delete
    fun deleteAthletes(vararg athletes: DatabaseBadmintonAthlete)

    @Update
    fun updateAthletes(vararg athletes: DatabaseBadmintonAthlete)

    @Query("UPDATE badminton_athletes SET filter = 0")
    override fun resetAthleteFilters()

    @Query("UPDATE badminton_athletes SET filter = 1 WHERE id = :id")
    override fun updateAthleteFilter(id: Int)

    @Query("SELECT * FROM badminton_athletes")
    fun getCurrentAthletes(): List<DatabaseBadmintonAthlete>

    @Query("SELECT * FROM badminton_athletes")
    fun getAthletes(): LiveData<List<DatabaseBadmintonAthlete>>

    @Query("SELECT badminton_athletes.*, countries.id as country_id, countries.name as country_name, countries.alphatwo as country_alphatwo FROM badminton_athletes JOIN countries ON badminton_athletes.nationality = countries.id")
    fun getAthletesWithCountry(): LiveData<List<DatabaseBadmintonAthleteWithCountry>>

    /**
     * events
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEvents(vararg events: DatabaseBadmintonEvent)

    @Delete
    fun deleteEvents(vararg events: DatabaseBadmintonEvent)

    @Update
    fun updateEvents(vararg events: DatabaseBadmintonEvent)

    @Query("UPDATE badminton_events SET filter = 0")
    override fun resetEventFilters()

    @Query("UPDATE badminton_events SET filter = 1 WHERE id = :id")
    override fun updateEventFilter(id: Int)

    @Query("UPDATE badminton_events SET list = 0")
    override fun resetEventListStatus()

    @Query("UPDATE badminton_events SET list = :status WHERE id = :id")
    override fun changeEventListStatus(status: Boolean, id: Int)

    @Query("SELECT * FROM badminton_events")
    fun getCurrentEvents(): List<DatabaseBadmintonEvent>

    @Query("SELECT * FROM badminton_events")
    fun getEvents(): LiveData<List<DatabaseBadmintonEvent>>

    /**
     * event categories
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEventCategories(vararg categories: DatabaseBadmintonEventCategory)

    @Delete
    fun deleteEventCategories(vararg categories: DatabaseBadmintonEventCategory)

    @Update
    fun updateEventCategories(vararg categories: DatabaseBadmintonEventCategory)

    @Query("UPDATE badminton_event_categories SET filter = 0")
    override fun resetEventCategoryFilters()

    @Query("UPDATE badminton_event_categories SET filter = 1 WHERE id = :id")
    override fun updateEventCategoryFilter(id: Int)

    @Query("SELECT * FROM badminton_event_categories")
    fun getCurrentEventCategories(): List<DatabaseBadmintonEventCategory>

    @Query("SELECT * FROM badminton_event_categories")
    fun getEventCategories(): LiveData<List<DatabaseBadmintonEventCategory>>

    @Query("SELECT * FROM badminton_event_categories WHERE main_category_id IS NULL")
    fun getMainEventCategories(): LiveData<List<DatabaseBadmintonEventCategory>>

    /**
     * entries
     */
    @Query("DELETE FROM badminton_entries")
    override fun clearEntries()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEntries(vararg entries: DatabaseBadmintonEntry)

    /**
     * filtered events
     */
    @Query("SELECT badminton_events.*, badminton_event_categories.id as category_id, badminton_event_categories.name as category_name, badminton_event_categories.filter as category_filter, countries.id as country_id, countries.name as country_name, countries.alphatwo as country_alphatwo FROM badminton_events JOIN badminton_event_categories ON badminton_event_categories.id = badminton_events.category JOIN countries ON countries.id = badminton_events.country WHERE badminton_events.list = 1")
    fun getFilteredEvents(): LiveData<List<DatabaseBadmintonFilteredEvent>>
}

