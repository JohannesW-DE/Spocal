package dev.weinsheimer.sportscalendar

import android.app.Application
import dev.weinsheimer.sportscalendar.di.appModule
import dev.weinsheimer.sportscalendar.di.databaseModule
import dev.weinsheimer.sportscalendar.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class SportsCalendarApplication : Application()
{
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        startKoin {
            androidLogger()
            androidContext(this@SportsCalendarApplication)
            modules(listOf(databaseModule, networkModule, appModule))
        }
    }
}