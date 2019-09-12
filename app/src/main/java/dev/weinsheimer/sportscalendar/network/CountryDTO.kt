package dev.weinsheimer.sportscalendar.network

import dev.weinsheimer.sportscalendar.database.model.DatabaseCountry

/**
 * Models
 */

data class NetworkCountryContainer(
    val countries: List<NetworkCountry>
)

data class NetworkCountry(
    val id: Int,
    val name: String,
    val alphatwo: String?
)

/**
 * Extensions
 */

fun NetworkCountryContainer.asDatabaseModel(): Array<DatabaseCountry> {
    return countries.map {
        DatabaseCountry(
            id = it.id,
            name = it.name,
            alphatwo = it.alphatwo
        )
    }.toTypedArray()
}