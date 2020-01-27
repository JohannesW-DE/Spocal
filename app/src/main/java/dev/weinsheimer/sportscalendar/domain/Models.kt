package dev.weinsheimer.sportscalendar.domain

import dev.weinsheimer.sportscalendar.util.Sport
import java.util.*

data class Country(val id: Int,
                   val name: String,
                   val alphatwo: String?
) {
    override fun toString(): String = name
}

data class Athlete(val id: Int,
                   val name: String,
                   val filter: Boolean
) {
    override fun toString(): String = name
}

data class EventCategory(val id: Int,
                         val name: String,
                         val filter: Boolean,
                         val mainCategory: Int?
) {
    override fun toString(): String = name
}

data class Event(
    val id: Int,
    val name: String,
    val filter: Boolean,
    val category: Int
) {
    override fun toString(): String = name
}

data class CalendarListItem(
    val id: Int,
    val sport: Sport,
    val name: String,
    val dateFrom: Date,
    val dateTo: Date,
    val category: String,
    val athletes: List<Athlete>,
    val details: MutableMap<String, Any?> = mutableMapOf(), // f.e. surface of a tennis event
    val country: Country,
    var expanded: Boolean = false
) {
    override fun toString(): String = name
}

