package dev.weinsheimer.sportscalendar.cycling

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.weinsheimer.sportscalendar.TestUtil
import dev.weinsheimer.sportscalendar.network.NetworkCyclingEventCategoriesContainer
import dev.weinsheimer.sportscalendar.network.NetworkCyclingEventsContainer
import dev.weinsheimer.sportscalendar.network.asDatabaseModel


class CyclingTestUtil : TestUtil() {
    override fun populateAthletes() {}

    override fun populateEventCategories() {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val json = read("cycling_events_categories_seed.json")
        moshi.adapter(NetworkCyclingEventCategoriesContainer::class.java).fromJson(json)?.let {
            database.cyclingDao.insertEventCategories(*it.asDatabaseModel())
        }
    }

    override fun populateEvents() {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val json = read("cycling_events_seed.json")
        moshi.adapter(NetworkCyclingEventsContainer::class.java).fromJson(json)?.let {
            database.cyclingDao.insertEvents(*it.asDatabaseModel())
        }
    }

    override fun modifyEntries() {}

    override fun modify() {
        database.cyclingDao.updateEventCategoryFilter(2)

        database.cyclingDao.updateEventFilter(3)
        database.cyclingDao.changeEventListStatus(true, 3)
    }
}
