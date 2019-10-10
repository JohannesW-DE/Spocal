package dev.weinsheimer.sportscalendar.cycling

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import dev.weinsheimer.sportscalendar.database.SpocalDB
import dev.weinsheimer.sportscalendar.database.dao.CountryDao
import dev.weinsheimer.sportscalendar.database.dao.CyclingDao
import dev.weinsheimer.sportscalendar.database.model.*
import dev.weinsheimer.sportscalendar.di.databaseTestModule
import dev.weinsheimer.sportscalendar.util.observeOnce
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.test.KoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
class CyclingDatabaseTest : KoinTest {
    private lateinit var countryDao: CountryDao
    private lateinit var cyclingDao: CyclingDao

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        loadKoinModules(databaseTestModule)
        val database: SpocalDB = get()

        CyclingTestUtil().apply {
            seed()
            modify()
        }

        countryDao = database.countryDao
        cyclingDao = database.cyclingDao
    }

    @After
    fun after() {}

    /**
     * Events
     */
    @Test
    fun testResetEventFilters() {
        cyclingDao.getEvents().observeOnce { events ->
            Truth.assertThat(events.filter { it.filter }).isNotEmpty()
        }
        cyclingDao.resetEventFilters()
        cyclingDao.getEvents().observeOnce { events ->
            Truth.assertThat(events.filter { it.filter }).isEmpty()
        }
    }

    @Test
    fun testUpdateEventFilter() {
        cyclingDao.getEvents().observeOnce { events ->
            Truth.assertThat(events.find { it.id == 1 }?.filter).isFalse()
        }
        cyclingDao.updateEventFilter(1)
        cyclingDao.getEvents().observeOnce { events ->
            Truth.assertThat(events.find { it.id == 1 }?.filter).isTrue()
        }
    }

    @Test
    fun testResetEventListStatus() {
        cyclingDao.getEvents().observeOnce { events ->
            Truth.assertThat(events.filter { it.list }).isNotEmpty()
        }
        cyclingDao.resetEventListStatus()
        cyclingDao.getEvents().observeOnce { events ->
            Truth.assertThat(events.filter { it.list }).isEmpty()
        }
    }

    @Test
    fun testChangeEventListStatus() {
        cyclingDao.getEvents().observeOnce { events ->
            Truth.assertThat(events.filter { it.list }).isNotEmpty()
        }
        cyclingDao.changeEventListStatus(true, 1)
        cyclingDao.changeEventListStatus(false, 3)
        cyclingDao.getEvents().observeOnce { events ->
            Truth.assertThat(events.find { it.id == 1 }?.list).isTrue()
            Truth.assertThat(events.find { it.id == 3 }?.list).isFalse()
        }
    }

    /**
     * Event categories
     */
    @Test
    fun testResetEventCategoryFilters() {
        cyclingDao.getEventCategories().observeOnce { eventCategories ->
            Truth.assertThat(eventCategories.filter { it.filter }).isNotEmpty()
        }
        cyclingDao.resetEventCategoryFilters()
        cyclingDao.getEventCategories().observeOnce { eventCategories ->
            Truth.assertThat(eventCategories.filter { it.filter }).isEmpty()
        }
    }

    @Test
    fun testUpdateEventCategoryFilter() {
        cyclingDao.getEventCategories().observeOnce { eventCategories ->
            Truth.assertThat(eventCategories.find { it.id == 3 }?.filter).isFalse()
        }
        cyclingDao.updateEventCategoryFilter(3)
        cyclingDao.getEventCategories().observeOnce { eventCategories ->
            Truth.assertThat(eventCategories.find { it.id == 3 }?.filter).isTrue()
        }
    }

    /**
     * Filtered events
     */
    @Test
    fun testGetFilteredEvents() {
        lateinit var country: DatabaseCountry
        lateinit var eventCategory: DatabaseCyclingEventCategory
        lateinit var event: DatabaseCyclingEvent

        countryDao.getCountries().observeOnce { countries ->
            countries.find { it.id == 11 }?.let {
                country = it
            }

        }
        cyclingDao.getEventCategories().observeOnce { eventCategories ->
            eventCategories.find { it.id == 6 }?.let {
                eventCategory = it
            }

        }
        cyclingDao.getEvents().observeOnce { events ->
            events.find { it.id == 3 }?.let {
                event = it
            }
        }

        Truth.assertThat(country).isNotNull()
        Truth.assertThat(eventCategory).isNotNull()
        Truth.assertThat(event).isNotNull()

        cyclingDao.getFilteredEvents().observeOnce { filteredEvents ->
            Truth.assertThat(filteredEvents.size).isEqualTo(1)

            filteredEvents.first().let { filteredEvent ->
                Truth.assertThat(filteredEvent.country).isEqualTo(country)
                Truth.assertThat(filteredEvent.category).isEqualTo(eventCategory)
                Truth.assertThat(filteredEvent.event).isEqualTo(event)
            }
        }
    }
}