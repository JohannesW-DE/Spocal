package dev.weinsheimer.sportscalendar.network

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import dev.weinsheimer.sportscalendar.database.dao.CyclingDao_Impl
import dev.weinsheimer.sportscalendar.repository.BadmintonRepository
import dev.weinsheimer.sportscalendar.repository.CountryRepository
import dev.weinsheimer.sportscalendar.repository.CyclingRepository
import dev.weinsheimer.sportscalendar.repository.TennisRepository
import dev.weinsheimer.sportscalendar.util.RefreshException
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent
import org.koin.core.inject

class RefreshWorker(appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams), KoinComponent {

    companion object {
        const val WORK_NAME = "RefreshWorker"
        const val WORK_NAME_UNIQUE = "UniqueRefreshWorker"
    }

    private val countryRepository: CountryRepository by inject()
    private val badmintonRepository: BadmintonRepository by inject()
    private val tennisRepository: TennisRepository by inject()
    private val cyclingRepository: CyclingRepository by inject()

    private suspend fun refresh() {
        countryRepository.refreshCountries()
        println("countries refreshed")
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
}