package dev.weinsheimer.sportscalendar.badminton

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.weinsheimer.sportscalendar.database.SpocalDB
import dev.weinsheimer.sportscalendar.database.dao.BadmintonDao
import dev.weinsheimer.sportscalendar.database.dao.CountryDao
import dev.weinsheimer.sportscalendar.database.model.DatabaseBadmintonEntry
import dev.weinsheimer.sportscalendar.database.model.DatabaseBadmintonEvent
import dev.weinsheimer.sportscalendar.database.model.DatabaseBadmintonEventCategory
import dev.weinsheimer.sportscalendar.database.model.DatabaseCountry
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
class BadmintonDatabaseTest : KoinTest {
    private lateinit var countryDao: CountryDao
    private lateinit var badmintonDao: BadmintonDao

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        loadKoinModules(databaseTestModule)
        val database: SpocalDB = get()

        BadmintonTestUtil().apply {
            seed()
            modify()
            modifyEntries()
        }

        countryDao = database.countryDao
        badmintonDao = database.badmintonDao
    }

    @After
    fun after() {}

    /**
     * Athletes
     */
    @Test
    fun testResetAthleteFilters() {
        badmintonDao.getAthletes().observeOnce { athletes ->
            assertThat(athletes.filter { it.filter }).isNotEmpty()
        }
        badmintonDao.resetAthleteFilters()
        badmintonDao.getAthletes().observeOnce { athletes ->
            assertThat(athletes.filter { it.filter }).isEmpty()
        }
    }

    @Test
    fun testUpdateAthleteFilter() {
        badmintonDao.getAthletes().observeOnce { athletes ->
            assertThat(athletes.find { it.id == 1 }?.filter).isFalse()
        }
        badmintonDao.updateAthleteFilter(1)
        badmintonDao.getAthletes().observeOnce { athletes ->
            assertThat(athletes.find { it.id == 1 }?.filter).isTrue()
        }
    }

    /**
     * Events
     */
    @Test
    fun testResetEventFilters() {
        badmintonDao.getEvents().observeOnce { events ->
            assertThat(events.filter { it.filter }).isNotEmpty()
        }
        badmintonDao.resetEventFilters()
        badmintonDao.getEvents().observeOnce { events ->
            assertThat(events.filter { it.filter }).isEmpty()
        }
    }

    @Test
    fun testUpdateEventFilter() {
        badmintonDao.getEvents().observeOnce { events ->
            assertThat(events.find { it.id == 1 }?.filter).isFalse()
        }
        badmintonDao.updateEventFilter(1)
        badmintonDao.getEvents().observeOnce { events ->
            assertThat(events.find { it.id == 1 }?.filter).isTrue()
        }
    }

    @Test
    fun testResetEventListStatus() {
        badmintonDao.getEvents().observeOnce { events ->
            assertThat(events.filter { it.list }).isNotEmpty()
        }
        badmintonDao.resetEventListStatus()
        badmintonDao.getEvents().observeOnce { events ->
            assertThat(events.filter { it.list }).isEmpty()
        }
    }

    @Test
    fun testChangeEventListStatus() {
        badmintonDao.getEvents().observeOnce { events ->
            assertThat(events.filter { it.list }).isNotEmpty()
        }
        badmintonDao.changeEventListStatus(true, 1)
        badmintonDao.changeEventListStatus(false, 5)
        badmintonDao.getEvents().observeOnce { events ->
            assertThat(events.find { it.id == 1 }?.list).isTrue()
            assertThat(events.find { it.id == 5 }?.list).isFalse()
        }
    }

    /**
     * Event categories
     */
    @Test
    fun testResetEventCategoryFilters() {
        badmintonDao.getEventCategories().observeOnce { eventCategories ->
            assertThat(eventCategories.filter { it.filter }).isNotEmpty()
        }
        badmintonDao.resetEventCategoryFilters()
        badmintonDao.getEventCategories().observeOnce { eventCategories ->
            assertThat(eventCategories.filter { it.filter }).isEmpty()
        }
    }

    @Test
    fun testUpdateEventCategoryFilter() {
        badmintonDao.getEventCategories().observeOnce { eventCategories ->
            assertThat(eventCategories.find { it.id == 6 }?.filter).isFalse()
        }
        badmintonDao.updateEventCategoryFilter(6)
        badmintonDao.getEventCategories().observeOnce { eventCategories ->
            assertThat(eventCategories.find { it.id == 6 }?.filter).isTrue()
        }
    }

    /**
     * Filtered events
     */
    @Test
    fun testGetFilteredEvents() {
        lateinit var country: DatabaseCountry
        lateinit var eventCategory: DatabaseBadmintonEventCategory
        lateinit var event: DatabaseBadmintonEvent

        countryDao.getCountries().observeOnce { countries ->
            countries.find { it.id == 5 }?.let {
                country = it
            }

        }
        badmintonDao.getEventCategories().observeOnce { eventCategories ->
            eventCategories.find { it.id == 2 }?.let {
                eventCategory = it
            }

        }
        badmintonDao.getEvents().observeOnce { events ->
            events.find { it.id == 5 }?.let {
                event = it
            }
        }

        assertThat(country).isNotNull()
        assertThat(eventCategory).isNotNull()
        assertThat(event).isNotNull()

        badmintonDao.getFilteredEvents().observeOnce { filteredEvents ->
            assertThat(filteredEvents.size).isEqualTo(2)

            filteredEvents.first().let { filteredEvent ->
                assertThat(filteredEvent.country).isEqualTo(country)
                assertThat(filteredEvent.category).isEqualTo(eventCategory)
                assertThat(filteredEvent.event).isEqualTo(event)
                assertThat(filteredEvent.entries.size).isEqualTo(2)
                assertThat(filteredEvent.entries.first().entry).isEqualTo(
                    DatabaseBadmintonEntry(
                        5,
                        3
                    )
                )
            }
        }
    }
}
