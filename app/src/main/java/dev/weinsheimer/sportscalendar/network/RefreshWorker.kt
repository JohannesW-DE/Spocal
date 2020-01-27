package dev.weinsheimer.sportscalendar.network

import android.content.Context
import androidx.work.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import dev.weinsheimer.sportscalendar.di.ChildWorkerFactory
import dev.weinsheimer.sportscalendar.repository.BadmintonRepository
import dev.weinsheimer.sportscalendar.repository.CountryRepository
import dev.weinsheimer.sportscalendar.repository.CyclingRepository
import dev.weinsheimer.sportscalendar.repository.TennisRepository
import dev.weinsheimer.sportscalendar.util.RefreshException
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass

class RefreshWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val countryRepository: CountryRepository,
    private val badmintonRepository: BadmintonRepository,
    private val tennisRepository: TennisRepository,
    private val cyclingRepository: CyclingRepository
): CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "RefreshWorker"
        const val WORK_NAME_UNIQUE = "UniqueRefreshWorker"
    }

    private suspend fun refresh() {
        println("uno")
        countryRepository.refreshCountries()
        println("dou")
        badmintonRepository.refresh()
        tennisRepository.refresh()
        cyclingRepository.refresh()
    }

    override suspend fun doWork(): Result {
        try {
            println("ddWork 1")
            refresh()
            println("doWork 2")
        } catch (e: RefreshException) {
            return Result.retry()
        }
        return Result.success()
    }

    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory
}





