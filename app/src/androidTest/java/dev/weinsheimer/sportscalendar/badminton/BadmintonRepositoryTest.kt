package dev.weinsheimer.sportscalendar.badminton

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.weinsheimer.sportscalendar.MockJsonInterceptor
import dev.weinsheimer.sportscalendar.database.dao.BadmintonDao
import dev.weinsheimer.sportscalendar.database.SpocalDB
import dev.weinsheimer.sportscalendar.di.databaseTestModule
import dev.weinsheimer.sportscalendar.domain.Athlete
import dev.weinsheimer.sportscalendar.domain.Event
import dev.weinsheimer.sportscalendar.network.NetworkFilterResultsRequest
import dev.weinsheimer.sportscalendar.observeOnce
import dev.weinsheimer.sportscalendar.repository.BadmintonRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get


@RunWith(AndroidJUnit4::class)
class BadmintonRepositoryTest : KoinTest {
    private lateinit var badmintonDao: BadmintonDao
    private lateinit var repo: BadmintonRepository

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        loadKoinModules(databaseTestModule)
        val database: SpocalDB = get()

        BadmintonTestUtil().apply {
            seed()
            modify()
        }
        badmintonDao = database.badmintonDao

        repo = get()

        loadKoinModules(module(override = true) {
            single<Interceptor>(override = true) { MockJsonInterceptor() } // T!
        })
    }

    @After
    fun after() {}

    @Test
    fun test_LiveData() {
        repo.athletes.observeOnce {
            assertThat(it.size).isEqualTo(7)
        }
        repo.eventCategories.observeOnce {
            assertThat(it.size).isEqualTo(6) // 6!! BECAUSE OF ADDITIONAL FILTERING!!
        }
        repo.mainEventCategories.observeOnce {
            assertThat(it.size).isEqualTo(3)
        }
        repo.events.observeOnce {
            assertThat(it.size).isEqualTo(6)
        }
        repo.calendarItems.observeOnce {
            assertThat(it.size).isEqualTo(2)
            assertThat(it.first().name).matches("TOTAL BWF World Championships 2019")
        }
    }

    @Test
    fun test_updateFilter()  {
        val athletes = listOf(Athlete(1, "", true))
        val events = listOf(Event(6, "", false, 0))

        runBlocking {
            repo.updateFilter(athletes, null, events)
        }

        repo.athletes.observeOnce {
            assertThat(it.find { athlete -> athlete.id == 1 }?.filter).isTrue()
            assertThat(it.find { event -> event.id == 6 }?.filter).isFalse()
        }
    }

    @Test
    fun test_hideEvent()  {
        runBlocking {
            repo.hideEvent(5)
        }
        badmintonDao.getEvents().observeOnce {
            assertThat(it.find { event -> event.id == 5 }?.list).isFalse()
        }
    }

    @Test
    fun test_createNetworkRequest() {
        repo.athletes.observeOnce {  }
        repo.eventCategories.observeOnce {  }
        repo.events.observeOnce {  }

        // careful! emptyList() because IDs are not in [5..10]
        assertThat(repo.createNetworkRequest()).isEqualTo(
            NetworkFilterResultsRequest(listOf(6, 7), listOf(6), emptyList()))
    }

    @Test
    fun test_refreshAthletes() {
        runBlocking {
            repo.refreshAthletes()
        }

        repo.athletes.observeOnce {
            assertThat(it.size).isEqualTo(3)
            assertThat(it.find { athlete -> athlete.id == 1 }?.name).isEqualTo("Lin Dann")
            assertThat(it.find { athlete -> athlete.id == 8 }?.name).isEqualTo("Anthony Sinisuka Ginting")
        }
    }

    @Test
    fun test_refreshEventCategories() {
        runBlocking {
            repo.refreshEventCategories()
        }

        repo.eventCategories.observeOnce {
            assertThat(it.size).isEqualTo(5)
            assertThat(it.find { eventCategory -> eventCategory.id == 10 }?.name)
                .isEqualTo("Stadtmeisterschaft")
        }
    }

    @Test
    fun test_refreshEvents() {
        runBlocking {
            repo.refreshEvents()
        }

        repo.events.observeOnce {
            assertThat(it.size).isEqualTo(3)
            assertThat(it.find { event -> event.id == 2 }?.name)
                .isEqualTo("TOTAL BWF World Championships 2222")
            assertThat(it.find { event -> event.id == 7 }?.name)
                .isEqualTo("DANISA Denmark Open 2019")
        }
    }

    @Test
    fun test_updateEvents() {
        runBlocking {
            repo.updateEvents()
        }

        repo.calendarItems.observeOnce { calendarItems ->
            assertThat(calendarItems.size).isEqualTo(2)
            assertThat(calendarItems[0].id).isEqualTo(5)
            assertThat(calendarItems[1].id).isEqualTo(6)
        }
    }
}