package dev.weinsheimer.sportscalendar.database

import androidx.room.*
import androidx.room.Database
import dev.weinsheimer.sportscalendar.database.dao.BadmintonDao
import dev.weinsheimer.sportscalendar.database.dao.CountryDao
import dev.weinsheimer.sportscalendar.database.dao.CyclingDao
import dev.weinsheimer.sportscalendar.database.dao.TennisDao
import dev.weinsheimer.sportscalendar.database.model.*

@Database(
    entities = [
        DatabaseCountry::class,
        DatabaseBadmintonAthlete::class,
        DatabaseBadmintonEventCategory::class,
        DatabaseBadmintonEvent::class,
        DatabaseBadmintonEntry::class,
        DatabaseCyclingAthlete::class,
        DatabaseCyclingEventCategory::class,
        DatabaseCyclingEvent::class,
        DatabaseCyclingEntry::class,
        DatabaseTennisAthlete::class,
        DatabaseTennisEventCategory::class,
        DatabaseTennisEvent::class,
        DatabaseTennisEntry::class
    ],
    version = 1)
@TypeConverters(Converters::class)
abstract class SpocalDB: RoomDatabase() {
    abstract val countryDao: CountryDao
    abstract val badmintonDao: BadmintonDao
    abstract val cyclingDao: CyclingDao
    abstract val tennisDao: TennisDao
}
