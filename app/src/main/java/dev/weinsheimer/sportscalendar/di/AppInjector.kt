package dev.weinsheimer.sportscalendar.di

import dev.weinsheimer.sportscalendar.repository.CountryRepository
import dev.weinsheimer.sportscalendar.repository.CyclingRepository
import dev.weinsheimer.sportscalendar.repository.TennisRepository
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel

import org.koin.dsl.module

import dev.weinsheimer.sportscalendar.repository.BadmintonRepository as BadmintonRepository

val appModule = module {
    single { CountryRepository() }
    single { BadmintonRepository() }
    single { CyclingRepository() }
    single { TennisRepository() }
    viewModel { SharedViewModel() }
}
