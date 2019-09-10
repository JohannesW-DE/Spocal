package dev.weinsheimer.sportscalendar.database

import androidx.room.Entity

interface DatabaseAthlete {
    val id: Int
}

interface NetworkAthleteContainerInterface {
    fun toDatabaseModel(): Array<DatabaseAthlete>
}