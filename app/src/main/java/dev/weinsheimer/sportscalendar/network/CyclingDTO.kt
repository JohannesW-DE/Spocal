package dev.weinsheimer.sportscalendar.network

import com.squareup.moshi.Json
import dev.weinsheimer.sportscalendar.database.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * ATHLETES
 */
data class NetworkCyclingAthletesContainer(
    val athletes: List<NetworkCyclingAthlete>)

data class NetworkCyclingAthlete(
    val id: Int,
    val name: String,
    val nationality: Int)

fun NetworkCyclingAthletesContainer.asDatabaseModel(): Array<DatabaseCyclingAthlete> {
    return athletes.map {
        DatabaseCyclingAthlete (id = it.id, name = it.name, gender = "m", filter = false, nationality = it.nationality)
    }.toTypedArray()
}

/**
 * EVENTS
 */
data class NetworkCyclingEventsContainer(
    val events: List<NetworkCyclingEvent>)

data class NetworkCyclingEvent(
    val id: Int,
    val name: String,
    @Json(name = "date_from") val dateFrom: String,
    @Json(name = "date_to") val dateTo: String,
    val category: Int,
    val country: Int
)

fun NetworkCyclingEventsContainer.asDatabaseModel(): Array<DatabaseCyclingEvent> {
    return events.map {
        DatabaseCyclingEvent (
            id = it.id,
            name = it.name,
            dateFrom = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(it.dateFrom),
            dateTo = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(it.dateTo),
            filter = false,
            list = false,
            category = it.category,
            country = it.country)
    }.toTypedArray()
}

/**
 * EVENTS / CATEGORIES
 */
data class NetworkCyclingEventCategoriesContainer(
    val categories: List<NetworkCyclingEventCategory>)

data class NetworkCyclingEventCategory(
    val id: Int,
    val name: String,
    @Json(name = "main_category") val mainCategory: Int?
)

fun NetworkCyclingEventCategoriesContainer.asDatabaseModel(): Array<DatabaseCyclingEventCategory> {
    return categories.map {
        DatabaseCyclingEventCategory(id = it.id, name = it.name, filter = false, mainCategory = it.mainCategory)
    }.toTypedArray()
}

/**
 * EVENTS / FILTERED
 */
data class NetworkCyclingFilter(@Json(name="athleteIDs") val athletes: List<Int>?,
                                @Json(name="eventIDs") val events: List<Int>?,
                                @Json(name="categoryIDs") val eventCategories: List<Int>?)

data class NetworkCyclingFilterResultContainer(
    val events: List<NetworkCyclingFilterResult>)

data class NetworkCyclingFilterResult(
    val id: Int,
    val entries: List<Int>?
)

fun NetworkCyclingFilterResultContainer.asDatabaseModel(): Array<DatabaseFilterResult> {
    return events.map {
        DatabaseFilterResult(id = 0, sport = "cycling", eventId = it.id, entries = it.entries)
    }.toTypedArray()
}