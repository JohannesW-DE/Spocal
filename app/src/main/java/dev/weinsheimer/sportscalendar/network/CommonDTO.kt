package dev.weinsheimer.sportscalendar.network

import com.squareup.moshi.Json

/**
 * Models
 */

data class NetworkFilterResultsContainer(
    val events: List<NetworkFilterResult>
)

data class NetworkFilterResult(
    val id: Int,
    val entries: List<Int>?
)

data class NetworkFilterResultsRequest(
    @Json(name="athleteIDs") val athletes: List<Int>?,
    @Json(name="eventIDs") val events: List<Int>?,
    @Json(name="categoryIDs") val eventCategories: List<Int>?
)





