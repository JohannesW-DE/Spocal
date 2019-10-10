package dev.weinsheimer.sportscalendar.general

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.weinsheimer.sportscalendar.badminton.BadmintonTestUtil
import dev.weinsheimer.sportscalendar.cycling.CyclingTestUtil
import dev.weinsheimer.sportscalendar.database.SpocalDB
import dev.weinsheimer.sportscalendar.di.databaseTestModule
import dev.weinsheimer.sportscalendar.util.observeOnce
import dev.weinsheimer.sportscalendar.tennis.TennisTestUtil
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.test.KoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
class ViewModelTest : KoinTest {
    private lateinit var viewModel: SharedViewModel

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
        CyclingTestUtil().apply {
            seed()
            modify()
        }
        TennisTestUtil().apply {
            seed()
            modify()
        }

        viewModel = get()
    }

    @After
    fun after() {}

    @Test
    fun testCalendarItems() {
        viewModel.calendarItems.observeOnce {
            assertThat(it.size).isEqualTo(6)
        }
    }
}