package dev.weinsheimer.sportscalendar.database

import androidx.lifecycle.LiveData
import androidx.room.*


abstract class GenericDaoClass<TAthlete: DatabaseAthlete>{
    abstract fun insert(vararg athletes: TAthlete)
    abstract fun delete(vararg athletes: TAthlete)
    abstract fun getCurrentAthletes(): List<TAthlete>
    abstract fun getAthletes(): LiveData<List<TAthlete>>
}

/*
abstract class GenericDaoClass<TAthlete: DatabaseAthlete> : DaoConsumer<TAthlete>, DaoProducer<TAthlete>

interface DaoConsumer<in TAthlete: DatabaseAthlete> {
    fun insert(vararg athletes: TAthlete)
    fun delete(vararg athletes: TAthlete)
}

interface DaoProducer<out TAthlete: DatabaseAthlete> {
    fun getCurrentAthletes(): List<TAthlete>
}

@Dao
interface GenericDao<in T : DatabaseAthlete> {
nConflict = OnConflictStrategy.IGNORE)
    fun insert(vararg athletes: T)

    @Delete
    fun delete(vararg athletes: T)
}
*/