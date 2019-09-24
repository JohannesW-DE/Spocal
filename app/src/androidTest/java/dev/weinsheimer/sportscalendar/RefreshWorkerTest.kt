package dev.weinsheimer.sportscalendar

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.google.common.truth.Truth
import dev.weinsheimer.sportscalendar.di.databaseTestModule
import dev.weinsheimer.sportscalendar.network.MockInterceptor
import dev.weinsheimer.sportscalendar.network.RefreshWorker
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.check.checkModules

@RunWith(AndroidJUnit4::class)
class RefreshWorkerTest : KoinTest {
    private lateinit var context: Context

    @Before
    fun before() {
        context = ApplicationProvider.getApplicationContext()
        // koin
        loadKoinModules(databaseTestModule)
    }

    private fun testRefreshWorker_success_fake(uri: String) : String {
        return when {
            uri.endsWith("countries") -> "{\"countries\": [{\"id\": 452, \"name\": \"Germany\", \"alphatwo\": \"de\", \"ioc\": \"GER\"}]}"
            uri.endsWith("badminton/athletes") -> "{\"athletes\": []}"
            uri.endsWith("badminton/events/categories") -> "{\"categories\": []}"
            uri.endsWith("badminton/events") -> "{\"events\": []}"
            uri.endsWith("tennis/athletes") -> "{\"athletes\": []}"
            uri.endsWith("tennis/events/categories") -> "{\"categories\": []}"
            uri.endsWith("tennis/events") -> "{\"events\": []}"
            uri.endsWith("cycling/events/categories") -> "{\"categories\": []}"
            uri.endsWith("cycling/events") -> "{\"events\": []}"
            else -> "{}"
        }
    }

    @Test
    fun testRefreshWorker_success() {
        // koin
        val mockInterceptor = MockInterceptor(::testRefreshWorker_success_fake)
        val module = module(override=true) {
            single<Interceptor> { mockInterceptor } // T!
        }
        loadKoinModules(module)

        val worker = TestListenableWorkerBuilder<RefreshWorker>(context).build()
        runBlocking {
            val result = worker.doWork()
            Truth.assertThat(result).isEqualTo(ListenableWorker.Result.success())
        }

        unloadKoinModules(module)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun testRefreshWorker_fail_fake(uri: String) : String {
        return "{}"
    }

    @Test
    fun testRefreshWorker_fail() {
        // koin
        val mockInterceptor = MockInterceptor(::testRefreshWorker_fail_fake)
        val module = module(override=true) {
            single<Interceptor> { mockInterceptor } // T!
        }
        loadKoinModules(module)

        val worker = TestListenableWorkerBuilder<RefreshWorker>(context).build()
        runBlocking {
            val result = worker.doWork()
            Truth.assertThat(result).isEqualTo(ListenableWorker.Result.retry())
        }

    }
}