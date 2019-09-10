package dev.weinsheimer.sportscalendar.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TennisDao : BaseDao {
    /**
     * athletes
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAthletes(vararg athletes: DatabaseTennisAthlete)

    @Delete
    fun deleteAthletes(vararg athletes: DatabaseTennisAthlete)

    @Update
    fun updateAthletes(vararg athletes: DatabaseTennisAthlete)

    @Query("UPDATE tennis_athletes SET filter = 0")
    override fun resetAthleteFilters()

    @Query("UPDATE tennis_athletes SET filter = 1 WHERE id = :id")
    override fun updateAthleteFilter(id: Int)

    @Query("SELECT * FROM tennis_athletes")
    fun getCurrentAthletes(): List<DatabaseTennisAthlete>

    @Query("SELECT * FROM tennis_athletes")
    fun getAthletes(): LiveData<List<DatabaseTennisAthlete>>

    @Query("SELECT tennis_athletes.*, countries.id as country_id, countries.name as country_name, countries.alphatwo as country_alphatwo FROM tennis_athletes JOIN countries ON tennis_athletes.nationality = countries.id")
    fun getAthletesWithCountry(): LiveData<List<DatabaseTennisAthleteWithCountry>>

    /**
     * events
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEvents(vararg events: DatabaseTennisEvent)

    @Delete
    fun deleteEvents(vararg events: DatabaseTennisEvent)

    @Update
    fun updateEvents(vararg events: DatabaseTennisEvent)

    @Query("UPDATE tennis_events SET filter = 0")
    override fun resetEventFilters()

    @Query("UPDATE tennis_events SET filter = 1 WHERE id = :id")
    override fun updateEventFilter(id: Int)

    @Query("UPDATE tennis_events SET list = 0")
    override fun resetEventListStatus()

    @Query("UPDATE tennis_events SET list = :status WHERE id = :id")
    override fun changeEventListStatus(status: Boolean, id: Int)

    @Query("SELECT * FROM tennis_events")
    fun getCurrentEvents(): List<DatabaseTennisEvent>

    @Query("SELECT * FROM tennis_events")
    fun getEvents(): LiveData<List<DatabaseTennisEvent>>

    /**
     * event categories
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEventCategories(vararg categories: DatabaseTennisEventCategory)

    @Delete
    fun deleteEventCategories(vararg categories: DatabaseTennisEventCategory)

    @Update
    fun updateEventCategories(vararg categories: DatabaseTennisEventCategory)

    @Query("UPDATE tennis_event_categories SET filter = 0")
    override fun resetEventCategoryFilters()

    @Query("UPDATE tennis_event_categories SET filter = 1 WHERE id = :id")
    override fun updateEventCategoryFilter(id: Int)

    @Query("SELECT * FROM tennis_event_categories")
    fun getCurrentEventCategories(): List<DatabaseTennisEventCategory>

    @Query("SELECT * FROM tennis_event_categories")
    fun getEventCategories(): LiveData<List<DatabaseTennisEventCategory>>

    /**
     * entries
     */
    @Query("DELETE FROM tennis_entries")
    override fun clearEntries()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEntries(vararg entries: DatabaseTennisEntry)

    /**
     * filtered events
     */
    @Query("SELECT tennis_events.*, tennis_event_categories.id as category_id, tennis_event_categories.name as category_name, tennis_event_categories.filter as category_filter, countries.id as country_id, countries.name as country_name, countries.alphatwo as country_alphatwo FROM tennis_events JOIN tennis_event_categories ON tennis_event_categories.id = tennis_events.category JOIN countries ON countries.id = tennis_events.country WHERE tennis_events.list = 1")
    fun getFilteredEvents(): LiveData<List<DatabaseTennisFilteredEvent>>
}