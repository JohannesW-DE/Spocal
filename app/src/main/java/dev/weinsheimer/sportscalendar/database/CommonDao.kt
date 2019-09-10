package dev.weinsheimer.sportscalendar.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CommonDao {
    /**
     * Country
     */
    @Query("SELECT * FROM countries")
    fun getCountries(): LiveData<List<DatabaseCountry>>

    @Query("SELECT * FROM countries")
    fun getCurrentCountries(): List<DatabaseCountry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCountries(vararg countries: DatabaseCountry)

    @Delete
    fun deleteCountries(vararg countries: DatabaseCountry)


    /**
     * filters
     */
    @Query("SELECT * FROM filters")
    fun getFilters(): LiveData<List<DatabaseFilter>>

    @Query("SELECT * FROM filters WHERE sport = :sport")
    fun getFilters(sport: String): LiveData<List<DatabaseFilter>>

    @Query("SELECT * FROM filters WHERE sport = :sport")
    fun getCurrentFilters(sport: String): List<DatabaseFilter>

    @Query("DELETE FROM filters WHERE sport = :sport")
    fun deleteFilters(sport: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilters(vararg filters: DatabaseFilter)

    @Query("DELETE FROM filters")
    fun deleteFilters()


    /**
     * filter results
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFilterResults(vararg results: DatabaseFilterResult)

    @Query("UPDATE filter_results SET entries = :entries WHERE sport = :sport AND event_id = :eventId")
    fun updateFilterResult(entries: String, sport: String, eventId: Int)

    @Query("UPDATE filter_results SET visible = 0 WHERE sport = :sport AND event_id = :eventId")
    fun updateFilterResultVisibility(sport: String, eventId: Int)

    @Query("DELETE FROM filter_results WHERE sport = :sport AND event_id NOT IN (:eventIds)")
    fun deleteFilterResults(sport: String, eventIds: List<Int>)

    @Query("SELECT * FROM filter_results WHERE sport = :sport")
    fun getCurrentFilterResults(sport: String): List<DatabaseFilterResult>


    /**
     * Debug
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg filters: DatabaseFilterResult)
}