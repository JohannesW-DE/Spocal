package dev.weinsheimer.sportscalendar

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.weinsheimer.sportscalendar.database.*
import dev.weinsheimer.sportscalendar.database.dao.BadmintonDao
import dev.weinsheimer.sportscalendar.database.dao.CountryDao
import dev.weinsheimer.sportscalendar.database.model.DatabaseBadmintonEntry
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import org.junit.Rule


@RunWith(AndroidJUnit4::class)
class DatabaseUnitTest {
    private lateinit var badmintonDao: BadmintonDao
    private lateinit var countryDao: CountryDao
    private lateinit var db: SpocalDB

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SpocalDB::class.java).build()

        badmintonDao = db.badmintonDao
        countryDao = db.countryDao

        TestUtil.populate(db)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun badminton_resetAthleteFilters() {
        badmintonDao.resetAthleteFilters()
        badmintonDao.getCurrentAthletes().forEach {
            assertThat(it.filter).isFalse()
        }
    }

    @Test
    @Throws(Exception::class)
    fun badminton_updateAthleteFilter() {
        badmintonDao.updateAthleteFilter(1)
        assertThat(badmintonDao.getCurrentAthletes().first().filter).isTrue()
    }

    @Test
    @Throws(Exception::class)
    fun badminton_getAthletesWithCountry() {
        val countries = countryDao.getCurrentCountries()
        val athletes = badmintonDao.getAthletesWithCountry()
        athletes.observeForever {  }
        assertThat(athletes.value!!.first().country).isEqualTo(countries.first())
    }

    @Test
    @Throws(Exception::class)
    fun badminton_resetEventListStatus() {
        badmintonDao.resetEventListStatus()
        assertThat(badmintonDao.getCurrentEvents().map { it.list }.distinct().first()).isEqualTo(false)
    }

    @Test
    @Throws(Exception::class)
    fun badminton_changeEventListStatus() {
        badmintonDao.changeEventListStatus(false, 1)
        assertThat(badmintonDao.getCurrentEvents().first().list).isEqualTo(false)
    }

    @Test
    @Throws(Exception::class)
    fun badminton_getFilteredEvents() {
        val category = badmintonDao.getCurrentEventCategories().first()
        val country = countryDao.getCurrentCountries().first()
        val event = badmintonDao.getCurrentEvents().first()
        val filteredEvents = badmintonDao.getFilteredEvents()
        filteredEvents.observeForever {  }
        assertThat(filteredEvents.value).isNotNull()
        assertThat(filteredEvents.value!!.first().category).isEqualTo(category)
        assertThat(filteredEvents.value!!.first().country).isEqualTo(country)
        assertThat(filteredEvents.value!!.first().event).isEqualTo(event)
        assertThat(filteredEvents.value!!.first().entries.size).isEqualTo(2)
        assertThat(filteredEvents.value!!.first().entries.first()).isEqualTo(
            DatabaseBadmintonEntry(
                1,
                1
            )
        )
    }
}
