package dev.weinsheimer.sportscalendar.badminton

import dev.weinsheimer.sportscalendar.database.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.weinsheimer.sportscalendar.TestUtil
import dev.weinsheimer.sportscalendar.network.*


class BadmintonTestUtil : TestUtil() {
    override fun read(file: String) : String {
        return context.assets.open(file).bufferedReader().use{
            it.readText()
        }
    }

    override fun populateAthletes() {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val json = read("badminton_athletes_seed.json")
        moshi.adapter(NetworkBadmintonAthletesContainer::class.java).fromJson(json)?.let {
            database.badmintonDao.insertAthletes(*it.asDatabaseModel())
        }
    }

    override fun populateEventCategories() {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val json = read("badminton_events_categories_seed.json")
        moshi.adapter(NetworkBadmintonEventCategoriesContainer::class.java).fromJson(json)?.let {
            database.badmintonDao.insertEventCategories(*it.asDatabaseModel())
        }
    }

    override fun populateEvents() {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val json = read("badminton_events_seed.json")
        moshi.adapter(NetworkBadmintonEventsContainer::class.java).fromJson(json)?.let {
            database.badmintonDao.insertEvents(*it.asDatabaseModel())
        }
    }

    override fun modifyEntries() {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val json = read("badminton_entries_seed.json")
        moshi.adapter(NetworkFilterResultsContainer::class.java).fromJson(json)?.let { container ->
            container.events.forEach { event ->
                database.badmintonDao.updateEventFilter(event.id)
                event.entries?.forEach { athleteId ->
                    database.badmintonDao.insertEntries(DatabaseBadmintonEntry(event.id, athleteId))
                }
            }
        }
    }

    override fun modify() {
        database.badmintonDao.updateAthleteFilter(6)
        database.badmintonDao.updateAthleteFilter(7)

        database.badmintonDao.updateEventCategoryFilter(11)
        database.badmintonDao.updateEventCategoryFilter(12)

        database.badmintonDao.updateEventFilter(6)
        database.badmintonDao.changeEventListStatus(true, 5)
        database.badmintonDao.changeEventListStatus(true, 6)
    }
}
