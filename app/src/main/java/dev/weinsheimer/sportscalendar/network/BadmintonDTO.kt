package dev.weinsheimer.sportscalendar.network

import com.squareup.moshi.Json
import dev.weinsheimer.sportscalendar.database.*
import dev.weinsheimer.sportscalendar.database.model.DatabaseBadmintonAthlete
import dev.weinsheimer.sportscalendar.database.model.DatabaseBadmintonEvent
import dev.weinsheimer.sportscalendar.database.model.DatabaseBadmintonEventCategory
import dev.weinsheimer.sportscalendar.database.model.DatabaseFilterResult
import java.text.SimpleDateFormat

/**
 * ATHLETES
 */
data class NetworkBadmintonAthletesContainer(
    val athletes: List<NetworkBadmintonAthlete>
) : NetworkAthleteContainerInterface {
    override fun toDatabaseModel(): Array<DatabaseAthlete> {
        return athletes.map {
            DatabaseBadmintonAthlete(
                id = it.id,
                name = it.name,
                gender = it.gender,
                filter = false,
                nationality = it.nationality
            )
        }.toTypedArray()
    }
}

data class NetworkBadmintonAthlete(
    val id: Int,
    val name: String,
    val gender: String,
    val nationality: Int)

fun NetworkBadmintonAthletesContainer.asDatabaseModel(): Array<DatabaseBadmintonAthlete> {
    return athletes.map {
        DatabaseBadmintonAthlete(
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
data class NetworkBadmintonEventsContainer(
    val events: List<NetworkBadmintonEvent>)

data class NetworkBadmintonEvent(
    val id: Int,
    val name: String,
    @Json(name = "date_from") val dateFrom: String,
    @Json(name = "date_to") val dateTo: String,
    val category: Int,
    val city: String,
    val country: Int
)

fun NetworkBadmintonEventsContainer.asDatabaseModel(): Array<DatabaseBadmintonEvent> {
    return events.map {
        DatabaseBadmintonEvent(
            id = it.id,
            name = it.name,
            dateFrom = SimpleDateFormat("yyyy-MM-dd").parse(it.dateFrom),
            dateTo = SimpleDateFormat("yyyy-MM-dd").parse(it.dateTo),
            city = it.city,
            filter = false,
            list = false,
            category = it.category,
            country = it.country
        )
    }.toTypedArray()
}

/**
 * EVENTS / CATEGORIES
 */
data class NetworkBadmintonEventCategoriesContainer(
    val categories: List<NetworkBadmintonEventCategory>)

data class NetworkBadmintonEventCategory(
    val id: Int,
    val name: String,
    @Json(name = "main_category") val mainCategory: Int?
)

fun NetworkBadmintonEventCategoriesContainer.asDatabaseModel(): Array<DatabaseBadmintonEventCategory> {
    return categories.map {
        DatabaseBadmintonEventCategory(
            id = it.id,
            name = it.name,
            filter = false,
            mainCategory = it.mainCategory
        )
    }.toTypedArray()
}

/**
 * EVENTS / FILTERED
 */
data class NetworkBadmintonFilter(@Json(name="athleteIDs") val badmintonAthletes: List<Int>?,
                                  @Json(name="eventIDs") val badmintonEvents: List<Int>?,
                                  @Json(name="categoryIDs") val badmintonCategories: List<Int>?)

data class NetworkBadmintonFilterResultContainer(
    val events: List<NetworkBadmintonFilterResult>)

data class NetworkBadmintonFilterResult(
    val id: Int,
    val entries: List<Int>?
)

fun NetworkBadmintonFilterResultContainer.asDatabaseModel(): Array<DatabaseFilterResult> {
    return events.map {
        DatabaseFilterResult(
            id = 0,
            sport = "badminton",
            eventId = it.id,
            entries = it.entries
        )
    }.toTypedArray()
}
