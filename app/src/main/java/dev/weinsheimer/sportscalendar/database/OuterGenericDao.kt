package dev.weinsheimer.sportscalendar.database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
abstract class OuterGenericDao : GenericDaoClass<DatabaseBadmintonAthlete>() {
    @Query("SELECT * FROM badminton_athletes")
    abstract override fun getAthletes(): LiveData<List<DatabaseBadmintonAthlete>>

    @Query("SELECT * FROM badminton_athletes")
    abstract override fun getCurrentAthletes(): List<DatabaseBadmintonAthlete>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract override fun insert(vararg athletes: DatabaseBadmintonAthlete)

    @Delete
    abstract override fun delete(vararg athletes: DatabaseBadmintonAthlete)
}

