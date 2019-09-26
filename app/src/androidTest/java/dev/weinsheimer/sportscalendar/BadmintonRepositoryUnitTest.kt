package dev.weinsheimer.sportscalendar

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.weinsheimer.sportscalendar.database.dao.BadmintonDao
import dev.weinsheimer.sportscalendar.database.SpocalDB
import dev.weinsheimer.sportscalendar.di.databaseTestModule
import dev.weinsheimer.sportscalendar.domain.Athlete
import dev.weinsheimer.sportscalendar.network.ApiService
import dev.weinsheimer.sportscalendar.network.NetworkFilterResultsRequest
import dev.weinsheimer.sportscalendar.repository.BadmintonRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class BadmintonRepositoryUnitTest : KoinTest {
    private lateinit var badmintonDao: BadmintonDao

    private val database: SpocalDB by inject()
    private val repo: BadmintonRepository by inject()
    private val testUtil = TestUtil(database)

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        loadKoinModules(databaseTestModule)
        testUtil.prepareForBadminton()
        testUtil.prepareForBadmintonDatabaseAndRepositoryUnitTest()

        badmintonDao = database.badmintonDao

        repo.athletes.observeForever {  }
        repo.eventCategories.observeForever {  }
        repo.events.observeForever {  }
        repo.calendarItems.observeForever {  }

        assertThat(badmintonDao.getCurrentAthletes().size).isEqualTo(7)
    }

    @After
    fun after() {
        database.close()
    }

    @Test
    fun test_LiveData() {
        assertThat(repo.athletes.value?.size).isEqualTo(7)
        assertThat(repo.eventCategories.value?.size).isEqualTo(12)
        assertThat(repo.mainEventCategories.value?.size).isEqualTo(3)
        assertThat(repo.events.value?.size).isEqualTo(6)
        assertThat(repo.calendarItems.value?.size).isEqualTo(2)

        val calendarItem = repo.calendarItems.value?.first()
        assertThat(repo.calendarItems.value?.first()).isNotNull()
        if (calendarItem != null) {
            assertThat(calendarItem.name).matches("TOTAL BWF World Championships 2019")
            assertThat(calendarItem.category).matches("BWF World Championships")
            assertThat(calendarItem.athletes.size).isEqualTo(2)
            assertThat(calendarItem.details).containsEntry("city", "Basel")
            assertThat(calendarItem.athletes.first().name).isEqualTo("Viktor Axelsen")
        }
    }

    //@Test
    fun test_updateFilter()  {
        val athletes = listOf(Athlete(1, "Athlete #1", false))
        runBlocking {
            repo.updateFilter(athletes, null, null)
        }
        assertThat(badmintonDao.getCurrentAthletes().first().filter).isTrue()
        assertThat(badmintonDao.getCurrentAthletes().filter { it.filter }.size).isEqualTo(1)
        assertThat(badmintonDao.getCurrentEventCategories().filter { it.filter }.size).isEqualTo(0)
        assertThat(badmintonDao.getCurrentEvents().filter { it.filter }.size).isEqualTo(0)
    }

    //@Test
    fun test_hideEvent()  {
        runBlocking {
            repo.hideEvent(1)
        }
        assertThat(badmintonDao.getCurrentEvents().first().list).isFalse()
    }


    //@Test
    fun test_createNetworkRequest() {
        assertThat(repo.createNetworkRequest()).isEqualTo(NetworkFilterResultsRequest(listOf(2), listOf(2), emptyList()))
    }

    //@Test
    @Throws(IOException::class)
    fun test_refreshAthletes_allNew() {
        runBlocking {
            repo.refreshAthletes()
        }
        assertThat(badmintonDao.getCurrentAthletes().size).isEqualTo(2)
        assertThat(badmintonDao.getCurrentAthletes().first().id).isEqualTo(3)
        assertThat(badmintonDao.getCurrentAthletes().last().id).isEqualTo(4)
    }

    //@Test
    fun test_refreshEventCategories() {

    }

    //@Test
    fun test_refreshEvents() {

    }

    //@Test
    fun test_updateEvents() {
    }
}