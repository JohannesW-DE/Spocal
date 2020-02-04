package dev.weinsheimer.sportscalendar.badminton


import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth
import dev.weinsheimer.sportscalendar.MainActivityTestRule
import dev.weinsheimer.sportscalendar.R
import dev.weinsheimer.sportscalendar.RecyclerViewMatcher
import dev.weinsheimer.sportscalendar.TestDispatcher
import dev.weinsheimer.sportscalendar.di.databaseTestModule
import dev.weinsheimer.sportscalendar.di.provideRetrofit
import dev.weinsheimer.sportscalendar.ui.CalendarAdapter
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest


@LargeTest
@RunWith(AndroidJUnit4::class)
class BadmintonInstrumentedTest : KoinTest {

    private var mockWebServer = MockWebServer()

    @Rule
    @JvmField
    var mActivityTestRule = MainActivityTestRule()

    @Before
    fun before() {
        loadKoinModules(databaseTestModule)

        /*
        BadmintonTestUtil().apply {
            seed()
        }
        */

        mockWebServer.dispatcher = TestDispatcher()
        mockWebServer.start(8888)

        loadKoinModules(module {
            single(override=true) { provideRetrofit(mockWebServer.url("/").toString()) } // T!
        })

        mActivityTestRule.launchActivity(null)
    }

    @After
    fun after() {
        mockWebServer.shutdown()
    }

    fun withRecyclerView(recyclerViewId: Int): RecyclerViewMatcher? {
        return RecyclerViewMatcher(recyclerViewId)
    }

    @Test
    fun addBadmintonAthlete() {
        val itemViewTypeMonth = 0
        val itemViewTypeEvent = 1

        // CalendarFragment
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
        onView(withText(R.string.menu_badminton)).perform(click())
        // BadmintonFilterFragment
        onView(withId(R.id.fsaAutoCompleteTextView)).perform(typeText("Lin"))
        Thread.sleep(5000)
        onView(withText("Lin Dan"))
            .inRoot(withDecorView(not(`is`(mActivityTestRule.activity.window.decorView))))
            .perform(click())
        onView(withId(R.id.fsaButton)).perform(click())
        onView(withId(R.id.floatingActionButton)).perform(click())
        // CalendarFragment
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        onView(withId(R.id.floatingActionButton)).perform(click())
        Thread.sleep(5000)

        // assert adapter
        val adapter =
            mActivityTestRule.activity.findViewById<RecyclerView>(R.id.calendar_recyclerView).adapter
        Truth.assertThat(adapter).isNotNull()
        Truth.assertThat(adapter?.itemCount).isEqualTo(3) // month & event
        Truth.assertThat(adapter?.getItemViewType(0)).isEqualTo(itemViewTypeMonth)
        Truth.assertThat(adapter?.getItemViewType(1)).isEqualTo(itemViewTypeEvent)

        // assert view
        onView(withId(R.id.calendar_recyclerView))
            .perform(actionOnItemAtPosition<CalendarAdapter.EventViewHolder>(1, click()))
        onView(withText("DAIHATSU Indonesia Masters 2020")).check(matches(isDisplayed()))

        onView(withRecyclerView(R.id.calendar_recyclerView)?.atPosition(1))
            .check(matches(hasDescendant(withText("Lin Dan"))));

        //onView(withText("Lin Dan")).check(matches(isDisplayed()))
        Thread.sleep(2000)
    }

    @Test
    fun addBadmintonEvent() {
        val itemViewTypeMonth = 0
        val itemViewTypeEvent = 1

        // CalendarFragment
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
        onView(withText(R.string.menu_badminton)).perform(click())
        // BadmintonFilterFragment
        onView(withId(R.id.fseAutoCompleteTextView)).perform(typeText("Japan"))
        Thread.sleep(500)
        onView(withText("DAIHATSU YONEX Japan Open 2020"))
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
        onView(withText("DAIHATSU YONEX Japan Open 2020")).check(matches(isDisplayed()))
        Thread.sleep(2000)
    }

    //@Test
    fun addBadmintonCategories() {
        val itemViewTypeMonth = 0
        val itemViewTypeEvent = 1

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
