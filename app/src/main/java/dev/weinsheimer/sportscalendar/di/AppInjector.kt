package dev.weinsheimer.sportscalendar.di

import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.weinsheimer.sportscalendar.database.SpocalDB
import dev.weinsheimer.sportscalendar.network.ApiService
import dev.weinsheimer.sportscalendar.repository.CommonRepository
import dev.weinsheimer.sportscalendar.repository.CyclingRepository
import dev.weinsheimer.sportscalendar.repository.TennisRepository
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel

import org.koin.dsl.module

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import dev.weinsheimer.sportscalendar.repository.BadmintonRepository as BadmintonRepository

val appModule = module {
    single { CommonRepository(get()) }
    single { BadmintonRepository(get(), get()) }
    single { CyclingRepository(get(), get()) }
    single { TennisRepository(get(), get()) }
    viewModel { SharedViewModel(get(), get(), get(), get()) }
}
