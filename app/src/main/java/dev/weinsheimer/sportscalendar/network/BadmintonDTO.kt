package dev.weinsheimer.sportscalendar.network

import com.squareup.moshi.Json
import dev.weinsheimer.sportscalendar.database.*
import dev.weinsheimer.sportscalendar.database.model.DatabaseBadmintonAthlete
import dev.weinsheimer.sportscalendar.database.model.DatabaseBadmintonEvent
import dev.weinsheimer.sportscalendar.database.model.DatabaseBadmintonEventCategory
import java.text.SimpleDateFormat
import java.util.*

/**
 * Classes
 */

data class NetworkBadmintonAthletesContainer(
    val athletes: List<NetworkBadmintonAthlete>
)

data class NetworkBadmintonAthlete(
    val id: Int,
    val name: String,
    val gender: String,
    val nationality: Int
)

data class NetworkBadmintonEventCategoriesContainer(
    val categories: List<NetworkBadmintonEventCategory>)

data class NetworkBadmintonEventCategory(
    val id: Int,
    val name: String,
    @Json(name = "main_category") val mainCategory: Int?
)

data class NetworkBadmintonEventsContainer(
    val events: List<NetworkBadmintonEvent>)

data class NetworkBadmintonEvent(
    val id: Int,
    val name: String,
    @Json(name = "date_from") val dateFrom: String,
    @Json(name = "date_to") val dateTo: String,
    val city: String,
    val category: Int,
    val country: Int
)

/**
 * Extensions
 */

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

fun NetworkBadmintonEventCategoriesContainer.asDatabaseModel(): Array<DatabaseBadmintonEventCategory> {
    return categories.map {
        val name = when(it.name) {
            "Individual Tournaments" -> "BWF World Championships"
            "Team Tournaments" -> "BWF World Mixed Team Championships"
            "HSBC BWF World Tour Finals" -> "World Tour Finals"
            "HSBC BWF World Tour Super 1000" -> "Super 1000"
            "HSBC BWF World Tour Super 750" -> "Super 750"
            "HSBC BWF World Tour Super 500" -> "Super 500"
            "HSBC BWF World Tour Super 300" -> "Super 300"
            "BWF Tour Super 100" -> "Super 100"
            else -> it.name
        }
        DatabaseBadmintonEventCategory(
            id = it.id,
            name = name,
            filter = false,
            mainCategory = it.mainCategory
        )
    }.toTypedArray()
}

fun NetworkBadmintonEventsContainer.asDatabaseModel(): Array<DatabaseBadmintonEvent> {
    return events.map {
        DatabaseBadmintonEvent(
            id = it.id,
            name = it.name,
            dateFrom = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(it.dateFrom),
            dateTo = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(it.dateTo),
            city = it.city,
            filter = false,
            list = false,
            category = it.category,
            country = it.country
        )
    }.toTypedArray()
}
