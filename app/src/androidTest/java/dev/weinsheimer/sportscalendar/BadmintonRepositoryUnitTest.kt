package dev.weinsheimer.sportscalendar

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.weinsheimer.sportscalendar.database.BadmintonDao
import dev.weinsheimer.sportscalendar.database.SpocalDB
import dev.weinsheimer.sportscalendar.di.databaseModule
import dev.weinsheimer.sportscalendar.di.databaseTestModule
import dev.weinsheimer.sportscalendar.di.networkTestModule
import dev.weinsheimer.sportscalendar.domain.Athlete
import dev.weinsheimer.sportscalendar.domain.Country
import dev.weinsheimer.sportscalendar.network.Api
import dev.weinsheimer.sportscalendar.network.ApiService
import dev.weinsheimer.sportscalendar.network.NetworkFilterResultsRequest
import dev.weinsheimer.sportscalendar.repository.BadmintonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.test.KoinTest
import org.koin.test.inject
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class BadmintonRepositoryUnitTest : KoinTest {
    private lateinit var repo: BadmintonRepository
    private lateinit var badmintonDao: BadmintonDao

    private val database: SpocalDB by inject()
    private val apiService: ApiService by inject()

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        loadKoinModules(listOf(databaseTestModule, networkTestModule))

        repo = BadmintonRepository(database, apiService)
        TestUtil.populate(database)
        badmintonDao = database.badmintonDao

        repo.setup()
        repo.athletes.observeForever {  }
        repo.events.observeForever {  }
        repo.mainEventCategories.observeForever {  }
        repo.eventCategories.observeForever {  }
        repo.calendarItems.observeForever {  }
        repo.calendarItemsWithEntries.observeForever {  }
    }

    @After
    @Throws(IOException::class)
    fun after() {
        database.close()
    }

    @Test
    fun test_LiveData() {
        assertThat(repo.athletes.value?.size).isEqualTo(2)
        assertThat(repo.mainEventCategories.value?.size).isEqualTo(10)
        assertThat(repo.eventCategories.value?.first()?.name).isEqualTo("Event Category #5")
        assertThat(repo.eventCategories.value?.last()?.id).isEqualTo(10)
        assertThat(repo.calendarItemsWithEntries.value).isNotNull()
        val calendarItem = repo.calendarItemsWithEntries.value?.first()
        assertThat(calendarItem).isNotNull()
        if (calendarItem != null) {
            assertThat(calendarItem.name).matches("Event #1")
            assertThat(calendarItem.category).matches("Event Category #1")
            assertThat(calendarItem.entries?.size).isEqualTo(2)
            assertThat(calendarItem.details).containsEntry("city", "Anycity")
            assertThat(calendarItem.athletes.first().name).isEqualTo("Athlete #1")
        }
    }

    @Test
    fun test_updateFilter()  {
        val athletes = listOf(Athlete(1, "Athlete #1", "m", false, Country(1, "Country #1", "A1")))
        runBlocking {
            repo.updateFilter(athletes, null, null)
        }
        assertThat(badmintonDao.getCurrentAthletes().first().filter).isTrue()
        assertThat(badmintonDao.getCurrentAthletes().filter { it.filter }.size).isEqualTo(1)
        assertThat(badmintonDao.getCurrentEventCategories().filter { it.filter }.size).isEqualTo(0)
        assertThat(badmintonDao.getCurrentEvents().filter { it.filter }.size).isEqualTo(0)
    }

    @Test
    fun test_hideEvent()  {
        runBlocking {
            repo.hideEvent(1)
        }
        assertThat(badmintonDao.getCurrentEvents().first().list).isFalse()
    }


    @Test
    fun test_createNetworkRequest() {
        assertThat(repo.createNetworkRequest()).isEqualTo(NetworkFilterResultsRequest(listOf(2), listOf(2), emptyList()))
    }

    @Test
    @Throws(IOException::class)
    fun test_refreshAthletes_allNew() {
        runBlocking {
            repo.refreshAthletes()
        }
        assertThat(badmintonDao.getCurrentAthletes().size).isEqualTo(2)
        assertThat(badmintonDao.getCurrentAthletes().first().id).isEqualTo(3)
        assertThat(badmintonDao.getCurrentAthletes().last().id).isEqualTo(4)
    }

    @Test
    fun test_refreshEventCategories() {

    }

    @Test
    fun test_refreshEvents() {

    }

    @Test
    fun test_updateEvents() {
    }
}