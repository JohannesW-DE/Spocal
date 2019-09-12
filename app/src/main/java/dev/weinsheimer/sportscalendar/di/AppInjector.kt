package dev.weinsheimer.sportscalendar.di

import dev.weinsheimer.sportscalendar.repository.CountryRepository
import dev.weinsheimer.sportscalendar.repository.CyclingRepository
import dev.weinsheimer.sportscalendar.repository.TennisRepository
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel

import org.koin.dsl.module

import dev.weinsheimer.sportscalendar.repository.BadmintonRepository as BadmintonRepository

val appModule = module {
    single { CountryRepository(get()) }
    single { BadmintonRepository(get(), get()) }
    single { CyclingRepository(get(), get()) }
    single { TennisRepository(get(), get()) }
    viewModel { SharedViewModel(get(), get(), get(), get()) }
}
