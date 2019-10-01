package dev.weinsheimer.sportscalendar.badminton


import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import dev.weinsheimer.sportscalendar.database.SpocalDB
import dev.weinsheimer.sportscalendar.di.databaseTestModule
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.test.KoinTest
import org.koin.test.inject
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import com.google.common.truth.Truth
import dev.weinsheimer.sportscalendar.MainActivityTestRule
import dev.weinsheimer.sportscalendar.R
import dev.weinsheimer.sportscalendar.network.MockInterceptor
import dev.weinsheimer.sportscalendar.ui.CalendarAdapter
import okhttp3.Interceptor
import org.koin.dsl.module
import org.koin.test.get
import timber.log.Timber



@LargeTest
@RunWith(AndroidJUnit4::class)
class BadmintonInstrumentedTest : KoinTest {

    @Rule
    @JvmField
    var mActivityTestRule = MainActivityTestRule()

    @Before
    fun before() {
        loadKoinModules(databaseTestModule)

        BadmintonTestUtil().apply {
            seed()
        }
    }

    @After
    fun after() {}

    private fun addBadmintonAthlete_fake(uri: String) : String {
        return when {
            uri.endsWith("badminton/events") -> "{\"events\": [{\"id\": 1, \"entries\": [1]}]}"
            uri.endsWith("tennis/events") -> "{\"events\": []}"
            uri.endsWith("cycling/events") -> "{\"events\": []}"
            else -> "{}"
        }
    }

    private fun setup(interceptor: Interceptor) {
        loadKoinModules(module {
            single(override=true) { interceptor } // T!
        })
        mActivityTestRule.launchActivity(null)
    }

    @Test
    fun addBadmintonAthlete() {
        val itemViewTypeMonth = 0
        val itemViewTypeEvent = 1

        setup(MockInterceptor(::addBadmintonAthlete_fake))

        // CalendarFragment
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
        onView(withText(R.string.menu_badminton)).perform(click())
        // BadmintonFilterFragment
        onView(withId(R.id.fsaAutoCompleteTextView)).perform(typeText("Lin"))
        Thread.sleep(500)
        onView(withText("Lin Dan"))
            .inRoot(withDecorView(not(`is`(mActivityTestRule.activity.window.decorView))))
            .perform(click())
        onView(withId(R.id.fsaButton)).perform(click())
        onView(withId(R.id.floatingActionButton)).perform(click())
        // CalendarFragment
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        onView(withId(R.id.floatingActionButton)).perform(click())
        Thread.sleep(500)

        // assert adapter
        val adapter =
            mActivityTestRule.activity.findViewById<RecyclerView>(R.id.calendar_recyclerView).adapter
        Truth.assertThat(adapter).isNotNull()
        Truth.assertThat(adapter?.itemCount).isEqualTo(2) // month & event
        Truth.assertThat(adapter?.getItemViewType(0)).isEqualTo(itemViewTypeMonth)
        Truth.assertThat(adapter?.getItemViewType(1)).isEqualTo(itemViewTypeEvent)
        // assert view
        onView(withId(R.id.calendar_recyclerView))
            .perform(actionOnItemAtPosition<CalendarAdapter.EventViewHolder>(1, click()))
        onView(withText("YONEX German Open 2019")).check(matches(isDisplayed()))
        onView(withText("Lin Dan")).check(matches(isDisplayed()))
        Thread.sleep(2000)
    }

    private fun addBadmintonEvent_fake(uri: String) : String {
        return when {
            uri.endsWith("badminton/events") -> "{\"events\": [{\"id\": 3}]}"
            uri.endsWith("tennis/events") -> "{\"events\": []}"
            uri.endsWith("cycling/events") -> "{\"events\": []}"
            else -> "{}"
        }
    }

    @Test
    fun addBadmintonEvent() {
        val itemViewTypeMonth = 0
        val itemViewTypeEvent = 1

        setup(MockInterceptor(::addBadmintonEvent_fake))

        // CalendarFragment
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
        onView(withText(R.string.menu_badminton)).perform(click())
        // BadmintonFilterFragment
        onView(withId(R.id.fseAutoCompleteTextView)).perform(typeText("Japan"))
        Thread.sleep(500)
        onView(withText("DAIHATSU YONEX Japan Open 2019"))
            .inRoot(withDecorView(not(`is`(mActivityTestRule.activity.window.decorView))))
            .perform(click())
        onView(withId(R.id.fseButton)).perform(click())
        onView(withId(R.id.floatingActionButton)).perform(click())
        // CalendarFragment
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        onView(withId(R.id.floatingActionButton)).perform(click())
        Thread.sleep(500)

        // assert adapter
        val adapter =
            mActivityTestRule.activity.findViewById<RecyclerView>(R.id.calendar_recyclerView).adapter
        Truth.assertThat(adapter).isNotNull()
        Truth.assertThat(adapter?.itemCount).isEqualTo(2) // month & event
        Truth.assertThat(adapter?.getItemViewType(0)).isEqualTo(itemViewTypeMonth)
        Truth.assertThat(adapter?.getItemViewType(1)).isEqualTo(itemViewTypeEvent)
        // assert view
        onView(withText("DAIHATSU YONEX Japan Open 2019")).check(matches(isDisplayed()))
        Thread.sleep(2000)
    }

    private fun addBadmintonCategories_fake(uri: String) : String {
        return when {
            uri.endsWith("badminton/events") -> "{\"events\": [{\"id\": 1}, {\"id\": 2}, {\"id\": 4}]}"
            uri.endsWith("tennis/events") -> "{\"events\": []}"
            uri.endsWith("cycling/events") -> "{\"events\": []}"
            else -> "{}"
        }
    }

    //@Test
    fun addBadmintonCategories() {
        val itemViewTypeMonth = 0
        val itemViewTypeEvent = 1

        setup(MockInterceptor(::addBadmintonCategories_fake))

        // CalendarFragment
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
        onView(withText(R.string.menu_badminton)).perform(click())
        // BadmintonFilterFragment
        onView(withText("Super 1000")).perform(click())
        onView(withText("Super 300")).perform(click())
        onView(withId(R.id.floatingActionButton)).perform(click())
        // CalendarFragment
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        onView(withId(R.id.floatingActionButton)).perform(click())

        // assert adapter
        val adapter =
            mActivityTestRule.activity.findViewById<RecyclerView>(R.id.calendar_recyclerView).adapter
        Truth.assertThat(adapter).isNotNull()
        Truth.assertThat(adapter?.itemCount).isEqualTo(6) // month & event
        Truth.assertThat(adapter?.getItemViewType(0)).isEqualTo(itemViewTypeMonth)
        Truth.assertThat(adapter?.getItemViewType(1)).isEqualTo(itemViewTypeEvent)
        Truth.assertThat(adapter?.getItemViewType(2)).isEqualTo(itemViewTypeMonth)
        Truth.assertThat(adapter?.getItemViewType(3)).isEqualTo(itemViewTypeEvent)
        Truth.assertThat(adapter?.getItemViewType(4)).isEqualTo(itemViewTypeMonth)
        Truth.assertThat(adapter?.getItemViewType(5)).isEqualTo(itemViewTypeEvent)
        // assert view
        onView(withText("February")).check(matches(isDisplayed()))
        onView(withText("March")).check(matches(isDisplayed()))
        onView(withText("July")).check(matches(isDisplayed()))
        onView(withText("YONEX German Open 2019")).check(matches(isDisplayed()))
        onView(withText("YONEX All England Open Badminton Championships 2019")).check(matches(isDisplayed()))
        onView(withText("BLIBLI Indonesia Open 2019")).check(matches(isDisplayed()))
        Thread.sleep(2000)
    }
}
