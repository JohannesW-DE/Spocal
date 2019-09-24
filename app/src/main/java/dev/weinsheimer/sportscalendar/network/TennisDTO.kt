package dev.weinsheimer.sportscalendar.network

import com.squareup.moshi.Json
import dev.weinsheimer.sportscalendar.database.model.DatabaseTennisAthlete
import dev.weinsheimer.sportscalendar.database.model.DatabaseTennisEvent
import dev.weinsheimer.sportscalendar.database.model.DatabaseTennisEventCategory
import java.text.SimpleDateFormat
import java.util.*

/**
 * ATHLETES
 */
data class NetworkTennisAthletesContainer(
    val athletes: List<NetworkTennisAthlete>)

data class NetworkTennisAthlete(
    val id: Int,
    val name: String,
    val gender: String,
    val nationality: Int)

fun NetworkTennisAthletesContainer.asDatabaseModel(): Array<DatabaseTennisAthlete> {
    return athletes.map {
        DatabaseTennisAthlete(
            id = it.id,
            name = it.name,
            gender = it.gender,
            filter = false,
            nationality = it.nationality
        )
    }.toTypedArray()
}

/**
 * EVENTS
 */
data class NetworkTennisEventsContainer(
    val events: List<NetworkTennisEvent>)

data class NetworkTennisEvent(
    val id: Int,
    val association: String,
    val name: String,
    @Json(name = "date_from") val dateFrom: String,
    @Json(name = "date_to") val dateTo: String,
    val singles: Int?,
    val doubles: Int?,
    val indoor: Boolean?,
    val surface: String?,
    val city: String,
    val country: Int,
    val category: Int
)

fun NetworkTennisEventsContainer.asDatabaseModel(): Array<DatabaseTennisEvent> {
    return events.map {
        DatabaseTennisEvent(
            id = it.id,
            association = it.association,
            name = it.name,
            dateFrom = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(it.dateFrom),
            dateTo = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(it.dateTo),
            singles = it.singles,
            doubles = it.doubles,
            indoor = it.indoor,
            surface = it.surface,
            city = it.city,
            filter = false,
            list = false,
            country = it.country,
            category = it.category
        )
    }.toTypedArray()
}

/**
 * EVENTS / CATEGORIES
 */
data class NetworkTennisEventCategoriesContainer(
    val categories: List<NetworkTennisEventCategory>)

data class NetworkTennisEventCategory(
    val id: Int,
    val association: String?,
    val name: String
)

fun NetworkTennisEventCategoriesContainer.asDatabaseModel(): Array<DatabaseTennisEventCategory> {
    return categories.map {
        DatabaseTennisEventCategory(
            id = it.id,
            association = it.association,
            name = it.name,
            filter = false
        )
    }.toTypedArray()
}

