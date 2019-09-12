package dev.weinsheimer.sportscalendar.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.weinsheimer.sportscalendar.database.model.DatabaseCountry
import dev.weinsheimer.sportscalendar.database.model.DatabaseFilter
import dev.weinsheimer.sportscalendar.database.model.DatabaseFilterResult

@Dao
interface CountryDao {
    @Query("SELECT * FROM countries")
    fun getCountries(): LiveData<List<DatabaseCountry>>

    @Query("SELECT * FROM countries")
    fun getCurrentCountries(): List<DatabaseCountry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCountries(vararg countries: DatabaseCountry)

    @Delete
    fun deleteCountries(vararg countries: DatabaseCountry)
}