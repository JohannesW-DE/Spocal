package dev.weinsheimer.sportscalendar

import dev.weinsheimer.sportscalendar.database.*
import dev.weinsheimer.sportscalendar.database.model.*
import dev.weinsheimer.sportscalendar.util.asSimpleDate
import java.util.*

class TestUtil {
    companion object {
        fun createDatabaseCountry(id: Int = 1) : DatabaseCountry {
            return DatabaseCountry(
                id,
                "Country #$id",
                "A$id"
            )
        }

        fun createDatabaseBadmintonAthlete(id: Int = 1, nationality: Int = 1) : DatabaseBadmintonAthlete {
            return DatabaseBadmintonAthlete(
                id,
                "Athlete #$id",
                "m",
                false,
                nationality
            )
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
            return DatabaseBadmintonEventCategory(
                id,
                "Event Category #$id",
                false,
                null
            )
        }

        fun createDatabaseBadmintonEntry(eventId: Int, athleteId: Int): DatabaseBadmintonEntry {
            return DatabaseBadmintonEntry(
                eventId,
                athleteId
            )
        }


        fun populateForEspresso(database: SpocalDB) {
            database.clearAllTables()

            database.countryDao.insertCountries(
                DatabaseCountry(1, "China", "CN"),
                DatabaseCountry(2, "Denmark", "DK"),
                DatabaseCountry(3, "Japan", "JP"),
                DatabaseCountry(4, "Chinese Taipei", null),
                DatabaseCountry(5, "Germany", "DE"),
                DatabaseCountry(6, "United Kingdom", "GB"),
                DatabaseCountry(7, "Indonesia", "ID")
            )

            database.badmintonDao.insertAthletes(
                DatabaseBadmintonAthlete(1, "Lin Dan", "m", false, 1),
                DatabaseBadmintonAthlete(2, "Chen Long", "m", false, 1),
                DatabaseBadmintonAthlete(3, "Viktor Axelsen", "m", false, 2),
                DatabaseBadmintonAthlete(4, "Kento Momota", "m", false, 3),
                DatabaseBadmintonAthlete(5, "Tai Tzu Ying", "w", false, 4)
            )

            database.badmintonDao.insertEventCategories(
                DatabaseBadmintonEventCategory(2, "BWF World Championships", false, null),
                DatabaseBadmintonEventCategory(3, "BWF World Mixed Team Championships", false, null),
                DatabaseBadmintonEventCategory(5, "World Tour Finals", false, null),
                DatabaseBadmintonEventCategory(6, "Super 1000", false, null),
                DatabaseBadmintonEventCategory(7, "Super 750", false, null),
                DatabaseBadmintonEventCategory(8, "Super 500", false, null),
                DatabaseBadmintonEventCategory(9, "Super 300", false, null),
                DatabaseBadmintonEventCategory(10, "Super 100", false, null)
            )

            database.badmintonDao.insertEvents(
                DatabaseBadmintonEvent(1, "YONEX German Open 2019", "2019-02-26".asSimpleDate(), "2019-03-03".asSimpleDate(), "Muelheim an der Ruhr", false, false, 5, 9),
                DatabaseBadmintonEvent(2, "YONEX All England Open Badminton Championships 2019", "2019-03-06".asSimpleDate(), "2019-03-10".asSimpleDate(), "Birmingham", false, false, 6, 6),
                DatabaseBadmintonEvent(3, "DAIHATSU YONEX Japan Open 2019", "2019-07-23".asSimpleDate(), "2019-07-28".asSimpleDate(), "Tokyo", false, false, 3, 7),
                DatabaseBadmintonEvent(4, "BLIBLI Indonesia Open 2019", "2019-07-16".asSimpleDate(), "2019-07-21".asSimpleDate(), "Jakarta", false, false, 7, 6)
            )

            database.badmintonDao.insertEntries(
                DatabaseBadmintonEntry(1, 1)
            )
        }

        fun populate(database: SpocalDB) {
            database.countryDao.insertCountries(createDatabaseCountry(1))
            database.countryDao.insertCountries(createDatabaseCountry(2))

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
