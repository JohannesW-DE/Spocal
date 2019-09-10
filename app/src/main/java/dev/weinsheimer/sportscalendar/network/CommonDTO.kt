package dev.weinsheimer.sportscalendar.network

import com.squareup.moshi.Json
import dev.weinsheimer.sportscalendar.database.DatabaseCountry
import dev.weinsheimer.sportscalendar.database.DatabaseFilterResult
import dev.weinsheimer.sportscalendar.domain.Country

/**
 * PING PONG
 */
data class NetworkPingContainer(
    val ping: String)

/**
 * COUNTRY
 */
data class NetworkCountryContainer(
    val countries: List<NetworkCountry>) // parses the return: {"countries":[...]}

data class NetworkCountry(
    val id: Int,
    val name: String,
    val alphatwo: String?)

fun NetworkCountryContainer.asDomainModel(): List<Country> {
    return countries.map {
        Country (
            id = it.id,
            name = it.name,
            alphatwo = it.alphatwo)
    }
}

fun NetworkCountryContainer.asDatabaseModel(): Array<DatabaseCountry> {
    return countries.map {
        DatabaseCountry (
            id = it.id,
            name = it.name,
            alphatwo = it.alphatwo)
    }.toTypedArray()
}

/**
 * EVENTS / FILTERED
 */
data class NetworkFilterResultsRequest(@Json(name="athleteIDs") val athletes: List<Int>?,
                                       @Json(name="eventIDs") val events: List<Int>?,
                                       @Json(name="categoryIDs") val eventCategories: List<Int>?)

data class NetworkFilterResultsContainer(
    val events: List<NetworkFilterResult>)

data class NetworkFilterResult(
    val id: Int,
    val entries: List<Int>?
)


fun NetworkFilterResultsContainer.asDatabaseModel(sport: String): Array<DatabaseFilterResult> {
    return events.map {
        DatabaseFilterResult(id = 0, sport = sport, eventId = it.id, entries = it.entries)
    }.toTypedArray()
}
