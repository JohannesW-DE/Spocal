package dev.weinsheimer.sportscalendar

import org.junit.Test

import org.junit.Assert.*
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import android.text.method.TextKeyListener.clear
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.weinsheimer.sportscalendar.database.DatabaseAthlete
import dev.weinsheimer.sportscalendar.database.GenericDaoClass
import dev.weinsheimer.sportscalendar.domain.Athlete
import dev.weinsheimer.sportscalendar.repository.BadmintonRepository
import dev.weinsheimer.sportscalendar.repository.BaseRepository
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun test() {
        val string = "awesome"
        assertThat(string).startsWith("awe")
        assertWithMessage("Without me, it's just aweso").that(string).contains("me")
    }

    @Test
    fun test_createNetworkRequest() {
        val repo = mock(BaseRepository::class.java)
        repo.athletes = MutableLiveData<Athlete>().apply { emptyList<Athlete>() }.
        repo.createNetworkRequest()
    }
}

