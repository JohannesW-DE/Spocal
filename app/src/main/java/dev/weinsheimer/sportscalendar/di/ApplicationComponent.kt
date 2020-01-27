package dev.weinsheimer.sportscalendar.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.work.WorkerFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.*
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.multibindings.IntoMap
import dev.weinsheimer.sportscalendar.BuildConfig
import dev.weinsheimer.sportscalendar.MainActivity
import dev.weinsheimer.sportscalendar.SportsCalendarApplication
import dev.weinsheimer.sportscalendar.database.SpocalDB
import dev.weinsheimer.sportscalendar.network.ApiService
import dev.weinsheimer.sportscalendar.network.RefreshWorker
import dev.weinsheimer.sportscalendar.repository.BadmintonRepository
import dev.weinsheimer.sportscalendar.repository.CountryRepository
import dev.weinsheimer.sportscalendar.repository.CyclingRepository
import dev.weinsheimer.sportscalendar.repository.TennisRepository
import dev.weinsheimer.sportscalendar.ui.*
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
class NetworkModule {
    @Provides
    fun provideApiService(): ApiService = Retrofit.Builder()
        .baseUrl(BuildConfig.SERVER_BASE_URL)
        .addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder().add(KotlinJsonAdapterFactory()).build()))
        .build().create(ApiService::class.java)
}


@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Singleton
    @Binds
    @IntoMap
    @ViewModelKey(SharedViewModel::class)
    abstract fun bindSharedViewModel(viewModel: SharedViewModel): ViewModel
}

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector
    abstract fun mainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun calendarFragment(): CalendarFragment

    @ContributesAndroidInjector
    abstract fun aboutFragment(): AboutFragment

    @ContributesAndroidInjector
    abstract fun badmintonFilterFragment(): BadmintonFilterFragment

    @ContributesAndroidInjector
    abstract fun cyclingFilterFragment(): CyclingFilterFragment

    @ContributesAndroidInjector
    abstract fun tennisFilterFragment(): TennisFilterFragment
}

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppAssistedInjectModule::class,
        ActivityModule::class,
        ApplicationModule::class,
        ViewModelModule::class,
        NetworkModule::class,
        WorkerModule::class
    ])
interface ApplicationComponent : AndroidInjector<SportsCalendarApplication> {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): ApplicationComponent
    }

    fun workerFactory(): DaggerWorkerFactory
}