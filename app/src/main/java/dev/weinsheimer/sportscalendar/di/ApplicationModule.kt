package dev.weinsheimer.sportscalendar.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.weinsheimer.sportscalendar.MainActivity
import dev.weinsheimer.sportscalendar.database.SpocalDB
import dev.weinsheimer.sportscalendar.repository.BadmintonRepository
import dev.weinsheimer.sportscalendar.repository.CountryRepository
import dev.weinsheimer.sportscalendar.repository.CyclingRepository
import dev.weinsheimer.sportscalendar.repository.TennisRepository
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import javax.inject.Singleton

//@Module(includes = [ApplicationModuleBinds::class])
@Module
class ApplicationModule {
    @Singleton
    @Provides
    fun provideRoomDatabase(context: Context) : SpocalDB {
        return Room.databaseBuilder(context, SpocalDB::class.java, "spocal").build()
    }
}


@Module
abstract class ApplicationModuleBinds {

    @Singleton
    @Binds
    abstract fun countryRepository(repo: CountryRepository): CountryRepository
    @Singleton
    @Binds
    abstract fun badmintonRepository(repo: BadmintonRepository): BadmintonRepository
    @Singleton
    @Binds
    abstract fun cyclingRepository(repo: CyclingRepository): CyclingRepository
    @Singleton
    @Binds
    abstract fun tennisRepository(repo: TennisRepository): TennisRepository




}
