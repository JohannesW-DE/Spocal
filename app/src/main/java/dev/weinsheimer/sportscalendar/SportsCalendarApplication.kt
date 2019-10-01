package dev.weinsheimer.sportscalendar

import android.app.Application
import android.content.Context
import androidx.work.*
import dev.weinsheimer.sportscalendar.di.appModule
import dev.weinsheimer.sportscalendar.di.databaseModule
import dev.weinsheimer.sportscalendar.di.networkModule
import dev.weinsheimer.sportscalendar.network.RefreshWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SportsCalendarApplication : Application()
{
    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        println("SportsCalendarApplication")

        startKoin {
            androidLogger()
            androidContext(this@SportsCalendarApplication)
            modules(listOf(databaseModule, networkModule, appModule))
        }

        applicationContext
            .getSharedPreferences("spocal", Context.MODE_PRIVATE)
            .getBoolean("testing", false).let { testing ->
                if (!testing) {
                    applicationScope.launch {
                        //setupRefreshWorker()
                    }
                }
            }
    }

    private fun setupRefreshWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val refreshRequest =
            PeriodicWorkRequestBuilder<RefreshWorker>(1, TimeUnit.DAYS)
                .addTag(RefreshWorker.WORK_NAME).setConstraints(constraints)
                .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            RefreshWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            refreshRequest
        )
    }
}