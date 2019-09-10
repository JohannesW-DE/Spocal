package dev.weinsheimer.sportscalendar.database

import android.content.Context
import androidx.room.*
import androidx.room.Database

@Database(
    entities = [
        DatabaseCountry::class,
        DatabaseBadmintonAthlete::class,
        DatabaseBadmintonEventCategory::class,
        DatabaseBadmintonEvent::class,
        DatabaseCyclingAthlete::class,
        DatabaseCyclingEventCategory::class,
        DatabaseCyclingEvent::class,
        DatabaseTennisAthlete::class,
        DatabaseTennisEventCategory::class,
        DatabaseTennisEvent::class,
        DatabaseFilter::class,
        DatabaseFilterResult::class,
        DatabaseBadmintonEntry::class,
        DatabaseTennisEntry::class
    ],
    version = 1)
@TypeConverters(Converters::class)
abstract class SpocalDB: RoomDatabase() {
    abstract val commonDao: CommonDao
    abstract val badmintonDao: BadmintonDao
    abstract val cyclingDao: CyclingDao
    abstract val tennisDao: TennisDao
    abstract val testDao: OuterGenericDao
}

private lateinit var INSTANCE: SpocalDB

fun getDatabase(context: Context): SpocalDB {
    synchronized(SpocalDB::class.java) {
        if (!::INSTANCE.isInitialized) {
            println("BUiLDING DB")
            println("BUiLDING DB")
            println("BUiLDING DB")
            INSTANCE = Room.databaseBuilder(context.applicationContext, SpocalDB::class.java, "spocal").build()
        }
        return INSTANCE
    }
}


