package dev.weinsheimer.sportscalendar.tennis

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.weinsheimer.sportscalendar.TestUtil
import dev.weinsheimer.sportscalendar.database.model.DatabaseBadmintonEntry
import dev.weinsheimer.sportscalendar.database.model.DatabaseTennisEntry
import dev.weinsheimer.sportscalendar.network.*


class TennisTestUtil : TestUtil() {
    override fun populateAthletes() {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val json = read("tennis_athletes_seed.json")
        moshi.adapter(NetworkTennisAthletesContainer::class.java).fromJson(json)?.let {
            database.tennisDao.insertAthletes(*it.asDatabaseModel())
        }
    }

    override fun populateEventCategories() {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val json = read("tennis_events_categories_seed.json")
        moshi.adapter(NetworkTennisEventCategoriesContainer::class.java).fromJson(json)?.let {
            database.tennisDao.insertEventCategories(*it.asDatabaseModel())
        }
    }

    override fun populateEvents() {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val json = read("tennis_events_seed.json")
        moshi.adapter(NetworkTennisEventsContainer::class.java).fromJson(json)?.let {
            database.tennisDao.insertEvents(*it.asDatabaseModel())
        }
    }

    override fun modifyEntries() {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val json = read("tennis_entries_seed.json")
        moshi.adapter(NetworkFilterResultsContainer::class.java).fromJson(json)?.let { container ->
            container.events.forEach { event ->
                database.tennisDao.updateEventFilter(event.id)
                event.entries?.forEach { athleteId ->
                    database.tennisDao.insertEntries(DatabaseTennisEntry(event.id, athleteId))
                }
            }
        }
    }

    override fun modify() {
        database.tennisDao.updateAthleteFilter(3)
        database.tennisDao.updateAthleteFilter(4)

        database.tennisDao.updateEventCategoryFilter(1)
        database.tennisDao.updateEventCategoryFilter(3)

        database.tennisDao.updateEventFilter(3)
        database.tennisDao.changeEventListStatus(true, 1)
        database.tennisDao.changeEventListStatus(true, 2)
        database.tennisDao.changeEventListStatus(true, 3)

        println("tennis modified")
    }
}
