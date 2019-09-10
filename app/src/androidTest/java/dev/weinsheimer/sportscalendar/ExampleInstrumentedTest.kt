package dev.weinsheimer.sportscalendar

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.Espresso.onView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import dev.weinsheimer.sportscalendar.ui.CalendarAdapter


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class ExampleInstrumentedTest {
    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun scrollToItemBelowFold_checkItsText() {
        // First scroll to the position that needs to be matched and click on it.
        onView(ViewMatchers.withId(R.id.calendar_recyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<CalendarAdapter.EventViewHolder>(0, click()))

        // Match the text in an item below the fold and check that it's displayed.
        onView(withText("Australian Open")).check(matches(isDisplayed()))
    }

}
