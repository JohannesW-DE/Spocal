package dev.weinsheimer.sportscalendar.tennis

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import dev.weinsheimer.sportscalendar.MockJsonInterceptor
import dev.weinsheimer.sportscalendar.database.SpocalDB
import dev.weinsheimer.sportscalendar.database.dao.TennisDao
import dev.weinsheimer.sportscalendar.di.databaseTestModule
import dev.weinsheimer.sportscalendar.domain.Athlete
import dev.weinsheimer.sportscalendar.domain.Event
import dev.weinsheimer.sportscalendar.domain.EventCategory
import dev.weinsheimer.sportscalendar.network.NetworkFilterResultsRequest
import dev.weinsheimer.sportscalendar.util.observeOnce
import dev.weinsheimer.sportscalendar.repository.TennisRepository
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
class TennisRepositoryTest : KoinTest {
    private lateinit var tennisDao: TennisDao
    private lateinit var repo: TennisRepository

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        loadKoinModules(databaseTestModule)
        val database: SpocalDB = get()

        TennisTestUtil().apply {
            seed()
            modify()
        }
        tennisDao = database.tennisDao

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
            Truth.assertThat(it.size).isEqualTo(4)
        }
        repo.eventCategories.observeOnce {
            Truth.assertThat(it.size).isEqualTo(11) // 6!! BECAUSE OF ADDITIONAL FILTERING!!
        }
        repo.mainEventCategories.observeOnce {
            Truth.assertThat(it.size).isEqualTo(11)
        }
        repo.events.observeOnce {
            Truth.assertThat(it.size).isEqualTo(4)
        }
        repo.calendarItems.observeOnce {
            Truth.assertThat(it.size).isEqualTo(3)
            Truth.assertThat(it.first().name).matches("Australian Open")
        }
    }

    @Test
    fun test_updateFilter()  {
        val athletes = listOf(Athlete(4, "", true))
        val eventCategories = listOf(EventCategory(3, "", true, null))
        val events = listOf(
            Event(3, "", true, 0),
            Event(4, "", true, 0))

        runBlocking {
            repo.updateFilter(athletes, eventCategories, events)
        }

        repo.athletes.observeOnce {
            Truth.assertThat(it.find { athlete -> athlete.id == 3 }?.filter).isFalse()
            Truth.assertThat(it.find { athlete -> athlete.id == 4 }?.filter).isTrue()
        }
        repo.eventCategories.observeOnce {
            Truth.assertThat(it.find { eventCategory -> eventCategory.id == 1 }?.filter).isFalse()
            Truth.assertThat(it.find { eventCategory -> eventCategory.id == 3 }?.filter).isTrue()
        }
        repo.events.observeOnce {
            Truth.assertThat(it.find { event -> event.id == 3 }?.filter).isTrue()
            Truth.assertThat(it.find { event -> event.id == 4 }?.filter).isTrue()
        }
    }

    @Test
    fun test_hideEvent()  {
        runBlocking {
            repo.hideEvent(2)
        }
        tennisDao.getEvents().observeOnce {
            Truth.assertThat(it.find { event -> event.id == 2 }?.list).isFalse()
        }
    }

    @Test
    fun test_createNetworkRequest() {
        repo.athletes.observeOnce {  }
        repo.eventCategories.observeOnce {  }
        repo.events.observeOnce {  }

        // careful! emptyList() because IDs are not in [5..10]
        Truth.assertThat(repo.createNetworkRequest()).isEqualTo(
            NetworkFilterResultsRequest(listOf(3, 4), listOf(3), listOf(1, 3))
        )
    }

    @Test
    fun test_refreshAthletes() {
        runBlocking {
            repo.refreshAthletes()
        }

        repo.athletes.observeOnce {
            Truth.assertThat(it.size).isEqualTo(3)
            Truth.assertThat(it.find { athlete -> athlete.id == 1 }?.name)
                .isEqualTo("Roger -Fedex- Federer")
            Truth.assertThat(it.find { athlete -> athlete.id == 2 }?.name)
                .isEqualTo("Alexandra Zverev")
            Truth.assertThat(it.find { athlete -> athlete.id == 5 }?.name)
                .isEqualTo("Kei Nishikori")
        }
    }

    @Test
    fun test_refreshEventCategories() {
        runBlocking {
            repo.refreshEventCategories()
        }

        repo.eventCategories.observeOnce {
            Truth.assertThat(it.size).isEqualTo(8)
            Truth.assertThat(it.find { eventCategory -> eventCategory.id == 1 }?.name)
                .isEqualTo("Stadtmeisterschaft")
            Truth.assertThat(it.find { eventCategory -> eventCategory.id == 12 }?.name)
                .isEqualTo("ATP - Hobbyturnier")
        }
    }

    @Test
    fun test_refreshEvents() {
        runBlocking {
            repo.refreshEvents()
        }

        repo.events.observeOnce {
            Truth.assertThat(it.size).isEqualTo(3)
            Truth.assertThat(it.find { event -> event.id == 1 }?.name)
                .isEqualTo("Australian Closed")
            Truth.assertThat(it.find { event -> event.id == 5 }?.name)
                .isEqualTo("China Open")
        }
    }

    @Test
    fun test_updateEvents() {
        runBlocking {
            repo.updateEvents()
        }

        repo.calendarItems.observeOnce { calendarItems ->
            Truth.assertThat(calendarItems.size).isEqualTo(2)
            Truth.assertThat(calendarItems[0].id).isEqualTo(1)
            Truth.assertThat(calendarItems[1].id).isEqualTo(4)
        }
    }
}