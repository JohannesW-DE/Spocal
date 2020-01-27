package dev.weinsheimer.sportscalendar

import android.app.Application
import androidx.work.*
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dev.weinsheimer.sportscalendar.di.ApplicationComponent
import dev.weinsheimer.sportscalendar.di.DaggerApplicationComponent
import dev.weinsheimer.sportscalendar.network.RefreshWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit


class SportsCalendarApplication : DaggerApplication(), Configuration.Provider
{
    private val applicationScope = CoroutineScope(Dispatchers.Default)

    private val appComponent by lazy {
        DaggerApplicationComponent.factory().create(applicationContext)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return appComponent
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        applicationScope.launch {
            setupRefreshWorker()
        }
    }


    private fun setupRefreshWorker() {
        WorkManager.initialize(this, Configuration.Builder().setWorkerFactory(appComponent.workerFactory()).build())

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val refreshRequest =
            PeriodicWorkRequestBuilder<RefreshWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(1, TimeUnit.DAYS)
                .addTag(RefreshWorker.WORK_NAME).setConstraints(constraints)
                .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            RefreshWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            refreshRequest
        )
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder().setWorkerFactory(appComponent.workerFactory()).build()
}