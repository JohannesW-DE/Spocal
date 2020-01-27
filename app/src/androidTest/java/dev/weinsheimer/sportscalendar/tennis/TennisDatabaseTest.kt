package dev.weinsheimer.sportscalendar.tennis

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import dev.weinsheimer.sportscalendar.database.SpocalDB
import dev.weinsheimer.sportscalendar.database.dao.CountryDao
import dev.weinsheimer.sportscalendar.database.dao.TennisDao
import dev.weinsheimer.sportscalendar.database.model.DatabaseCountry
import dev.weinsheimer.sportscalendar.database.model.DatabaseTennisEntry
import dev.weinsheimer.sportscalendar.database.model.DatabaseTennisEvent
import dev.weinsheimer.sportscalendar.database.model.DatabaseTennisEventCategory
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
class TennisDatabaseTest : KoinTest {
    private lateinit var countryDao: CountryDao
    private lateinit var tennisDao: TennisDao

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        loadKoinModules(databaseTestModule)
        val database: SpocalDB = get()

        TennisTestUtil().apply {
            seed()
            modify()
            modifyEntries()
        }

        countryDao = database.countryDao
        tennisDao = database.tennisDao
    }

    @After
    fun after() {}

    /**
     * Athletes
     */
    @Test
    fun testResetAthleteFilters() {
        tennisDao.getAthletes().observeOnce { athletes ->
            Truth.assertThat(athletes.filter { it.filter }).isNotEmpty()
        }
        tennisDao.resetAthleteFilters()
        tennisDao.getAthletes().observeOnce { athletes ->
            Truth.assertThat(athletes.filter { it.filter }).isEmpty()
        }
    }

    @Test
    fun testUpdateAthleteFilter() {
        tennisDao.getAthletes().observeOnce { athletes ->
            Truth.assertThat(athletes.find { it.id == 1 }?.filter).isFalse()
        }
        tennisDao.updateAthleteFilter(1)
        tennisDao.getAthletes().observeOnce { athletes ->
            Truth.assertThat(athletes.find { it.id == 1 }?.filter).isTrue()
        }
    }

    /**
     * Events
     */
    @Test
    fun testResetEventFilters() {
        tennisDao.getEvents().observeOnce { events ->
            Truth.assertThat(events.filter { it.filter }).isNotEmpty()
        }
        tennisDao.resetEventFilters()
        tennisDao.getEvents().observeOnce { events ->
            Truth.assertThat(events.filter { it.filter }).isEmpty()
        }
    }

    @Test
    fun testUpdateEventFilter() {
        tennisDao.getEvents().observeOnce { events ->
            Truth.assertThat(events.find { it.id == 4 }?.filter).isFalse()
        }
        tennisDao.updateEventFilter(4)
        tennisDao.getEvents().observeOnce { events ->
            Truth.assertThat(events.find { it.id == 4 }?.filter).isTrue()
        }
    }

    @Test
    fun testResetEventListStatus() {
        tennisDao.getEvents().observeOnce { events ->
            Truth.assertThat(events.filter { it.list }).isNotEmpty()
        }
        tennisDao.resetEventListStatus()
        tennisDao.getEvents().observeOnce { events ->
            Truth.assertThat(events.filter { it.list }).isEmpty()
        }
    }

    @Test
    fun testChangeEventListStatus() {
        tennisDao.getEvents().observeOnce { events ->
            Truth.assertThat(events.filter { it.list }).isNotEmpty()
        }
        tennisDao.changeEventListStatus(true, 1)
        tennisDao.changeEventListStatus(false, 4)
        tennisDao.getEvents().observeOnce { events ->
            Truth.assertThat(events.find { it.id == 1 }?.list).isTrue()
            Truth.assertThat(events.find { it.id == 4 }?.list).isFalse()
        }
    }

    /**
     * Event categories
     */
    @Test
    fun testResetEventCategoryFilters() {
        tennisDao.getEventCategories().observeOnce { eventCategories ->
            Truth.assertThat(eventCategories.filter { it.filter }).isNotEmpty()
        }
        tennisDao.resetEventCategoryFilters()
        tennisDao.getEventCategories().observeOnce { eventCategories ->
            Truth.assertThat(eventCategories.filter { it.filter }).isEmpty()
        }
    }

    @Test
    fun testUpdateEventCategoryFilter() {
        tennisDao.getEventCategories().observeOnce { eventCategories ->
            Truth.assertThat(eventCategories.find { it.id == 2 }?.filter).isFalse()
        }
        tennisDao.updateEventCategoryFilter(2)
        tennisDao.getEventCategories().observeOnce { eventCategories ->
            Truth.assertThat(eventCategories.find { it.id == 2 }?.filter).isTrue()
        }
    }

    /**
     * Filtered events
     */
    @Test
    fun testGetFilteredEvents() {
        lateinit var country: DatabaseCountry
        lateinit var eventCategory: DatabaseTennisEventCategory
        lateinit var event: DatabaseTennisEvent

        countryDao.getCountries().observeOnce { countries ->
            countries.find { it.id == 12 }?.let {
                country = it
            }

        }
        tennisDao.getEventCategories().observeOnce { eventCategories ->
            eventCategories.find { it.id == 1 }?.let {
                eventCategory = it
            }

        }
        tennisDao.getEvents().observeOnce { events ->
            events.find { it.id == 1 }?.let {
                event = it
            }
        }

        Truth.assertThat(country).isNotNull()
        Truth.assertThat(eventCategory).isNotNull()
        Truth.assertThat(event).isNotNull()

        tennisDao.getFilteredEvents().observeOnce { filteredEvents ->
            Truth.assertThat(filteredEvents.size).isEqualTo(3)

            filteredEvents.first().let { filteredEvent ->
                Truth.assertThat(filteredEvent.country).isEqualTo(country)
                Truth.assertThat(filteredEvent.category).isEqualTo(eventCategory)
                Truth.assertThat(filteredEvent.event).isEqualTo(event)
                Truth.assertThat(filteredEvent.entries.size).isEqualTo(2)
                Truth.assertThat(filteredEvent.entries.first().entry).isEqualTo(
                    DatabaseTennisEntry(
                        1,
                        2
                    )
                )
            }
        }
    }
}