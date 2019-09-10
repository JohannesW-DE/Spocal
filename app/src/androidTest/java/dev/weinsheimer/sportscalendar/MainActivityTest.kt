package dev.weinsheimer.sportscalendar


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.Espresso.onView
import org.junit.Before


@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    private lateinit var mActivity: MainActivity

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun before() {
        mActivity = mActivityTestRule.activity
    }

    @Test
    fun mainActivityTest() {
        val appCompatImageButton = onView(
            allOf(
                withContentDescription("Open navigation drawer"),
                childAtPosition(
                    allOf(
                        withId(R.id.action_bar),
                        childAtPosition(
                            withId(R.id.action_bar_container),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatImageButton.perform(click())

        val navigationMenuItemView = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.design_navigation_view),
                        childAtPosition(
                            withId(R.id.navigationView),
                            0
                        )
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        navigationMenuItemView.perform(click())

        onView(withId(R.id.athleteAutoCompleteTextView)).perform(scrollTo(), replaceText("Lin"), closeSoftKeyboard())
        onView(withText("Lin Dan"))
            .inRoot(withDecorView(not(`is`(mActivity.window.decorView))))
            .perform(click())
        /*
        val constraintLayout = onData(anything())
            .inAdapterView(withClassName(`is`("android.widget.ListPopupWindow$DropDownListView")))
            .atPosition(0)
        constraintLayout.perform(click())
         */

        onView(withText("Lin"))
            .inRoot(withDecorView(not(`is`(mActivityTestRule.activity.window.decorView))))
            .perform(click())

        val materialButton = onView(
            allOf(
                withId(R.id.button), withText("+"),
                childAtPosition(
                    allOf(
                        withId(R.id.athlete),
                        childAtPosition(
                            withId(R.id.constraintLayout),
                            1
                        )
                    ),
                    2
                )
            )
        )
        materialButton.perform(scrollTo(), click())

        val floatingActionButton = onView(
            allOf(
                withId(R.id.floatingActionButton),
                childAtPosition(
                    allOf(
                        withId(R.id.constraintLayout),
                        childAtPosition(
                            withId(R.id.scrollView),
                            0
                        )
                    ),
                    4
                )
            )
        )
        floatingActionButton.perform(scrollTo(), click())

        val appCompatImageButton2 = onView(
            allOf(
                withContentDescription("Navigate up"),
                childAtPosition(
                    allOf(
                        withId(R.id.action_bar),
                        childAtPosition(
                            withId(R.id.action_bar_container),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatImageButton2.perform(click())

        val floatingActionButton2 = onView(
            allOf(
                withId(R.id.floatingActionButton),
                childAtPosition(
                    allOf(
                        withId(R.id.outer_constraintLayout),
                        childAtPosition(
                            withId(R.id.navHostFragment),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        floatingActionButton2.perform(click())

        val constraintLayout2 = onView(
            allOf(
                withId(R.id.outer_constraintLayout),
                childAtPosition(
                    allOf(
                        withId(R.id.calendar_recyclerView),
                        childAtPosition(
                            withId(R.id.outer_constraintLayout),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        constraintLayout2.perform(click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
