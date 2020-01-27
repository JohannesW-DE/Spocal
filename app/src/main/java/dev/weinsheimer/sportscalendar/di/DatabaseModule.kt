package dev.weinsheimer.sportscalendar.di

/*
fun provideRoomDatabase(context: Context) : SpocalDB {
    return Room.databaseBuilder(context, SpocalDB::class.java, "spocal").build()
}

val databaseModule = module {
    single { provideRoomDatabase(androidContext()) }
}

val databaseTestModule = module(override = true) {
    single(override = true){ Room.inMemoryDatabaseBuilder(androidContext(), SpocalDB::class.java).allowMainThreadQueries().build() }
}
*/
