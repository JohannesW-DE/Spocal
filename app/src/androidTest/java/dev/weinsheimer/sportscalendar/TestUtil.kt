package dev.weinsheimer.sportscalendar

import dev.weinsheimer.sportscalendar.database.*
import java.time.Instant
import java.util.*

class TestUtil {
    companion object {
        fun createDatabaseCountry(id: Int = 1) : DatabaseCountry {
            return DatabaseCountry(id, "Country #$id", "A$id")
        }

        fun createDatabaseBadmintonAthlete(id: Int = 1, nationality: Int = 1) : DatabaseBadmintonAthlete {
            return DatabaseBadmintonAthlete(id, "Athlete #$id", "m", false, nationality)
        }

        fun createDatabaseBadmintonEvent(id: Int = 1, country: Int = 1, category: Int = 1) : DatabaseBadmintonEvent {
            return DatabaseBadmintonEvent(
                id,
                "Event #$id",
                Calendar.getInstance().time,
                Calendar.getInstance().time,
                "Anycity",
                filter = false,
                list = false,
                country = country,
                category = category
            )
        }

        fun createDatabaseBadmintonEventCategory(id: Int = 1) : DatabaseBadmintonEventCategory {
            return DatabaseBadmintonEventCategory(id, "Event Category #$id", false, null)
        }

        fun createDatabaseBadmintonEntry(eventId: Int, athleteId: Int): DatabaseBadmintonEntry {
            return DatabaseBadmintonEntry(eventId, athleteId)
        }

        fun populate(database: SpocalDB) {
            database.commonDao.insertCountries(createDatabaseCountry(1))
            database.commonDao.insertCountries(createDatabaseCountry(2))

            val athletes = listOf(
                createDatabaseBadmintonAthlete(1),
                createDatabaseBadmintonAthlete(2).apply { filter = true })
            database.badmintonDao.insertAthletes(*athletes.toTypedArray())

            (1..10).forEach {
                database.badmintonDao.insertEventCategories(createDatabaseBadmintonEventCategory(it))
            }

            val events = listOf(
                createDatabaseBadmintonEvent(1, 1, 1).apply { list = true },
                createDatabaseBadmintonEvent(2, 1, 1).apply { filter = true }
            )
            database.badmintonDao.insertEvents(*events.toTypedArray())

            val entries = listOf(
                createDatabaseBadmintonEntry(1, 1),
                createDatabaseBadmintonEntry(1, 2))
            database.badmintonDao.insertEntries(*entries.toTypedArray())
        }
    }
}
