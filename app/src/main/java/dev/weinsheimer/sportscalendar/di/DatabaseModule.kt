package dev.weinsheimer.sportscalendar.di

import android.content.Context
import androidx.room.Room
import dev.weinsheimer.sportscalendar.database.SpocalDB
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module


fun provideRoomDatabase(context: Context) : SpocalDB {
    return Room.databaseBuilder(context, SpocalDB::class.java, "spocal").build()
}

val databaseModule = module {
    single { provideRoomDatabase(androidContext()) }
}

val databaseTestModule = module(override = true) {
    single(override = true){ Room.inMemoryDatabaseBuilder(androidContext(), SpocalDB::class.java).allowMainThreadQueries().build() }
}