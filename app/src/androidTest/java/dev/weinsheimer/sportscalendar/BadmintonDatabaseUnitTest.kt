package dev.weinsheimer.sportscalendar

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.weinsheimer.sportscalendar.database.*
import dev.weinsheimer.sportscalendar.database.dao.BadmintonDao
import dev.weinsheimer.sportscalendar.database.dao.CountryDao
import dev.weinsheimer.sportscalendar.database.model.DatabaseBadmintonEntry
import dev.weinsheimer.sportscalendar.di.databaseTestModule
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Rule
import org.koin.core.context.loadKoinModules
import org.koin.test.KoinTest
import org.koin.test.inject


@RunWith(AndroidJUnit4::class)
class BadmintonDatabaseUnitTest : KoinTest {
    private lateinit var badmintonDao: BadmintonDao
    private lateinit var countryDao: CountryDao

    private val database: SpocalDB by inject()
    private val testUtil = TestUtil(database)

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        loadKoinModules(databaseTestModule)
        testUtil.prepareForBadminton()
        testUtil.prepareForBadmintonDatabaseAndRepositoryUnitTest()
        countryDao = database.countryDao
        badmintonDao = database.badmintonDao

    }

    @After
    fun after() {
        database.close()
    }

    /**
     * Athletes
     */

    @Test
    fun badminton_resetAthleteFilters() {
        badmintonDao.getAthletes().observeOnce { athletes ->
            assertThat(athletes.filter { it.filter }).isNotEmpty()
        }
        badmintonDao.resetAthleteFilters()
        badmintonDao.getAthletes().observeOnce { athletes ->
            assertThat(athletes.filter { it.filter }).isEmpty()
        }
    }

    @Test
    fun badminton_updateAthleteFilter() {
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
    fun badminton_resetEventFilters() {
        badmintonDao.getEvents().observeOnce { events ->
            assertThat(events.filter { it.filter }).isNotEmpty()
        }
        badmintonDao.resetEventFilters()
        badmintonDao.getEvents().observeOnce { events ->
            assertThat(events.filter { it.filter }).isEmpty()
        }
    }

    @Test
    fun badminton_updateEventFilter() {
        badmintonDao.getEvents().observeOnce { events ->
            assertThat(events.find { it.id == 1 }?.filter).isFalse()
        }
        badmintonDao.updateEventFilter(1)
        badmintonDao.getEvents().observeOnce { events ->
            assertThat(events.find { it.id == 1 }?.filter).isTrue()
        }
    }

    @Test
    fun badminton_resetEventListStatus() {
        badmintonDao.getEvents().observeOnce { events ->
            assertThat(events.filter { it.list }).isNotEmpty()
        }
        badmintonDao.resetEventListStatus()
        badmintonDao.getEvents().observeOnce { events ->
            assertThat(events.filter { it.list }).isEmpty()
        }
    }

    @Test
    fun badminton_changeEventListStatus() {
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
    fun badminton_resetEventCategoryFilters() {
        badmintonDao.getEventCategories().observeOnce { eventCategories ->
            assertThat(eventCategories.filter { it.filter }).isNotEmpty()
        }
        badmintonDao.resetEventCategoryFilters()
        badmintonDao.getEventCategories().observeOnce { eventCategories ->
            assertThat(eventCategories.filter { it.filter }).isEmpty()
        }
    }

    @Test
    fun badminton_updateEventCategoryFilter() {
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
    fun badminton_getFilteredEvents() {
        val country = countryDao.getCurrentCountries().find { it.id == 5 }
        val eventCategory = badmintonDao.getCurrentEventCategories().find { it.id == 2 }
        val event = badmintonDao.getCurrentEvents().find { it.id == 5 }

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
