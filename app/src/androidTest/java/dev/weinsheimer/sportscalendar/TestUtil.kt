package dev.weinsheimer.sportscalendar

import dev.weinsheimer.sportscalendar.database.*
import dev.weinsheimer.sportscalendar.database.model.*
import dev.weinsheimer.sportscalendar.util.asSimpleDate
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class TestUtil(private val database: SpocalDB) {
    init {
        database.countryDao.insertCountries(*badmintonCountries.toTypedArray())
    }

    fun prepareForBadminton() {
        database.badmintonDao.insertAthletes(*badmintonAthletes.toTypedArray())
        database.badmintonDao.insertEventCategories(*badmintonEventCategories.toTypedArray())
        database.badmintonDao.insertEvents(*badmintonEvents.toTypedArray())
    }

    fun prepareForBadmintonDatabaseAndRepositoryUnitTest() {
        database.badmintonDao.insertAthletes(
            DatabaseBadmintonAthlete(6, "Anders Antonsen", "m", true, 2),
            DatabaseBadmintonAthlete(7, "Li Xuerui", "w", true, 1)
        )

        database.badmintonDao.insertEventCategories(
            DatabaseBadmintonEventCategory(11, "Continental Circuit (Grade 3)", true, null),
            DatabaseBadmintonEventCategory(12, "International Challenge", true, 11)
        )

        database.badmintonDao.insertEvents(
            DatabaseBadmintonEvent(5, "TOTAL BWF World Championships 2019", "2019-08-19".asSimpleDate(), "2019-08-25".asSimpleDate(), "Basel", false, true, 5, 2),
            DatabaseBadmintonEvent(6, "VICTOR China Open 2019", "2019-09-17".asSimpleDate(), "2019-09-22".asSimpleDate(), "Changzhou", true, true, 1, 6)
        )

        database.badmintonDao.insertEntries(
            DatabaseBadmintonEntry(5, 3),
            DatabaseBadmintonEntry(5, 7)
        )
    }

    companion object {
        val badmintonCountries = listOf(
            DatabaseCountry(1, "China", "CN"),
            DatabaseCountry(2, "Denmark", "DK"),
            DatabaseCountry(3, "Japan", "JP"),
            DatabaseCountry(4, "Chinese Taipei", null),
            DatabaseCountry(5, "Germany", "DE"),
            DatabaseCountry(6, "United Kingdom", "GB"),
            DatabaseCountry(7, "Indonesia", "ID"),
            DatabaseCountry(8, "Switzerland", "CH")
        )

        val badmintonAthletes = listOf(
            DatabaseBadmintonAthlete(1, "Lin Dan", "m", false, 1),
            DatabaseBadmintonAthlete(2, "Chen Long", "m", false, 1),
            DatabaseBadmintonAthlete(3, "Viktor Axelsen", "m", false, 2),
            DatabaseBadmintonAthlete(4, "Kento Momota", "m", false, 3),
            DatabaseBadmintonAthlete(5, "Tai Tzu Ying", "w", false, 4)
        )

        val badmintonEventCategories = listOf(
            DatabaseBadmintonEventCategory(1, "BWF Tournaments / Major Events (Grade 1)", false, null),
            DatabaseBadmintonEventCategory(2, "BWF World Championships", false, 1),
            DatabaseBadmintonEventCategory(3, "BWF World Mixed Team Championships", false, 1),
            DatabaseBadmintonEventCategory(4, "BWF World Tour (Grade 2)", true, null),
            DatabaseBadmintonEventCategory(5, "World Tour Finals", false, 4),
            DatabaseBadmintonEventCategory(6, "Super 1000", false, 4),
            DatabaseBadmintonEventCategory(7, "Super 750", false, 4),
            DatabaseBadmintonEventCategory(8, "Super 500", false, 4),
            DatabaseBadmintonEventCategory(9, "Super 300", false, 4),
            DatabaseBadmintonEventCategory(10, "Super 100", false, 4)
        )

        val badmintonEvents = listOf(
            DatabaseBadmintonEvent(1, "YONEX German Open 2019", "2019-02-26".asSimpleDate(), "2019-03-03".asSimpleDate(), "Muelheim an der Ruhr", false, false, 5, 9),
            DatabaseBadmintonEvent(2, "YONEX All England Open Badminton Championships 2019", "2019-03-06".asSimpleDate(), "2019-03-10".asSimpleDate(), "Birmingham", false, false, 6, 6),
            DatabaseBadmintonEvent(3, "DAIHATSU YONEX Japan Open 2019", "2019-07-23".asSimpleDate(), "2019-07-28".asSimpleDate(), "Tokyo", false, false, 3, 7),
            DatabaseBadmintonEvent(4, "BLIBLI Indonesia Open 2019", "2019-07-16".asSimpleDate(), "2019-07-21".asSimpleDate(), "Jakarta", false, false, 7, 6)
        )
    }
}
