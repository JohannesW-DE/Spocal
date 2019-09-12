package dev.weinsheimer.sportscalendar.domain

import java.util.*

import dev.weinsheimer.sportscalendar.database.model.DatabaseFilter
import dev.weinsheimer.sportscalendar.util.Sport

data class Country(val id: Int,
                   val name: String,
                   val alphatwo: String?) {
    override fun toString(): String = name
}

data class Athlete(val id: Int,
                   val name: String,
                   val filter: Boolean
) {
    override fun toString(): String = name

    override fun equals(other: Any?): Boolean {
        if (other !is Athlete) return false
        if (this === other) return true
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return this.id
    }
}

@JvmName("athleteToDatabaseFilter")
fun List<Athlete>.asDatabaseFilter(sport: String): List<DatabaseFilter> {
    return map {
        DatabaseFilter(
            id = 0,
            sport = sport,
            type = "athlete",
            value = it.id
        )
    }
}

data class Event(
    val id: Int,
    val name: String,
    val filter: Boolean,
    val category: Int
) {
    override fun toString(): String = name

    override fun equals(other: Any?): Boolean {
        if (other !is Event) return false
        if (this === other) return true
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return this.id
    }
}

@JvmName("eventAsDatabaseFilter")
fun List<Event>.asDatabaseFilter(sport: String): List<DatabaseFilter> {
    return map {
        DatabaseFilter(
            id = 0,
            sport = sport,
            type = "event",
            value = it.id
        )
    }
}

@JvmName("eventAsCalendarListItem")
fun List<Event>.asCalendarListItem(sport: String): List<DatabaseFilter> {
    return map {
        //CalendarListItem(sport, it.id, it.name, it.c)
        DatabaseFilter(
            id = 0,
            sport = sport,
            type = "event",
            value = it.id
        )
    }
}

/**
 * EVENTS / CATEGORIES
 */
data class EventCategory(val id: Int,
                         val name: String,
                         val filter: Boolean,
                         val mainCategory: Int?) {

    override fun toString(): String = name

    override fun equals(other: Any?): Boolean {
        if (other !is EventCategory) return false
        if (this === other) return true
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return this.id
    }
}

@JvmName("eventCategoryAsDatabaseFilter")
fun List<EventCategory>.asDatabaseFilter(sport: String): List<DatabaseFilter> {
    return map {
        DatabaseFilter(
            id = 0,
            sport = sport,
            type = "category",
            value = it.id
        )
    }
}

/**
 * CALENDAR LIST ITEM
 */
data class CalendarListItem(
    val id: Int,
    val sport: Sport,
    val name: String,
    val dateFrom: Date,
    val dateTo: Date,
    val category: String,
    var entries: List<Int>?,
    var athletes: List<Athlete>,
    val details: MutableMap<String, Any?> = mutableMapOf(), // f.e. surface of a tennis event
    var expanded: Boolean = false,
    val country: Country
) {
    override fun toString(): String = name

    override fun equals(other: Any?): Boolean {
        if (other !is CalendarListItem) return false
        if (this === other) return true
        return (this.sport == other.sport) && (this.id == other.id) && (this.athletes == other.athletes)
    }

    override fun hashCode(): Int {
        return (this.sport.id + this.id).hashCode()
    }
}


data class FilterResult(
    val sport: String,
    val eventId: Int,
    val entries: List<Int>?
)






data class Filter(val sport: String,
                  val type: String,
                  val value: Int) {
    override fun toString(): String = "$sport / $type / $value"
}





/**
 * FilteredBadmintonEvent
 */
data class FilteredBadmintonEvent(
    val eventId: Int,
    val entries: List<Int>?
)

/**
 * CalendarListItem
 */

