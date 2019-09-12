package dev.weinsheimer.sportscalendar.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.weinsheimer.sportscalendar.database.model.DatabaseCountry

@Dao
interface CountryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCountries(vararg countries: DatabaseCountry)

    @Delete
    fun deleteCountries(vararg countries: DatabaseCountry)

    @Update
    fun updateCountries(vararg events: DatabaseCountry)

    @Query("SELECT * FROM countries")
    fun getCurrentCountries(): List<DatabaseCountry>

    @Query("SELECT * FROM countries")
    fun getCountries(): LiveData<List<DatabaseCountry>>

}