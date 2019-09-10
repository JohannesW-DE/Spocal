package dev.weinsheimer.sportscalendar

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.google.common.truth.Truth
import dev.weinsheimer.sportscalendar.database.BadmintonDao
import dev.weinsheimer.sportscalendar.database.SpocalDB
import dev.weinsheimer.sportscalendar.di.*
import dev.weinsheimer.sportscalendar.network.Api
import dev.weinsheimer.sportscalendar.network.ApiService
import dev.weinsheimer.sportscalendar.repository.BadmintonRepository
import dev.weinsheimer.sportscalendar.ui.CalendarAdapter
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.junit.MockitoJUnit.rule



@RunWith(AndroidJUnit4::class)
@LargeTest
class CalendarFragmentTest : KoinTest {
    private lateinit var repo: BadmintonRepository
    private lateinit var badmintonDao: BadmintonDao

    private val database: SpocalDB by inject()
    private val apiService: ApiService by inject()

    @get:Rule
    var rule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun before() {
        loadKoinModules(databaseTestModule)

        repo = BadmintonRepository(database, apiService)
        TestUtil.populate(database)
        badmintonDao = database.badmintonDao
        badmintonDao.changeEventListStatus(true, 1)
        badmintonDao.changeEventListStatus(true, 2)
    }

    @Test
    fun scrollToItemBelowFold_checkItsText() {
        // First scroll to the position that needs to be matched and click on it.
        /*
        Espresso.onView(ViewMatchers.withId(R.id.calendar_recyclerView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<CalendarAdapter.EventViewHolder>(0,
                    ViewActions.click()
                ))
*/
        // Match the text in an item below the fold and check that it's displayed.

        Truth.assertThat(badmintonDao.getCurrentEvents().size).isEqualTo(2)

        Espresso.onView(ViewMatchers.withText("Event #1"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))


    }

}