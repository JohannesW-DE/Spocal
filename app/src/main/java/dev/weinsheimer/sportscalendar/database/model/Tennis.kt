package dev.weinsheimer.sportscalendar.database.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import dev.weinsheimer.sportscalendar.database.DatabaseAthlete
import dev.weinsheimer.sportscalendar.domain.Athlete
import dev.weinsheimer.sportscalendar.domain.CalendarListItem
import dev.weinsheimer.sportscalendar.domain.Event
import dev.weinsheimer.sportscalendar.domain.EventCategory
import dev.weinsheimer.sportscalendar.util.Sport
import java.util.*

/**
 * Models
 */


/**
 * Extensions
 */


@Entity(tableName = "tennis_athletes")
data class DatabaseTennisAthlete constructor(
    @PrimaryKey(autoGenerate = false)
    override val id: Int,
    var name: String,
    var gender: String,
    var filter: Boolean,
    var nationality: Int
) : DatabaseAthlete

@JvmName("databaseTennisAthleteUpdate")
fun DatabaseTennisAthlete.update(athlete: DatabaseTennisAthlete) {
    this.name = athlete.name
    this.gender = athlete.gender
    this.nationality = athlete.nationality
}

data class DatabaseTennisAthleteWithCountry constructor(
    val id: Int,
    val name: String,
    val gender: String,
    val filter: Boolean,
    @Embedded(prefix = "country_")
    val country: DatabaseCountry
)

@JvmName("databaseTennisAthleteWithCountryAsDomainModel")
fun List<DatabaseTennisAthleteWithCountry>.asDomainModel(): List<Athlete> {
    return map {
        Athlete(id = it.id, name = it.name, gender = it.gender, filter = it.filter, nationality = it.country.asDomainModel())
    }
}

/**
 * EVENTS / CATEGORIES
 */
@Entity(tableName = "tennis_event_categories")
data class DatabaseTennisEventCategory constructor(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val association: String?,
    var name: String,
    val filter: Boolean)

@JvmName("databaseTennisEventCategoryAsDomainModel")
fun List<DatabaseTennisEventCategory>.asDomainModel(): List<EventCategory> {
    return map {
        var name = it.name
        it.association?.let { association ->
            name = association.toUpperCase() + " - " + name
        }
        EventCategory(id = it.id, name = name, filter = it.filter, mainCategory = null)
    }
}


@JvmName("databaseTennisEventCategoryUpdate")
fun DatabaseTennisEventCategory.update(eventCategory: DatabaseTennisEventCategory) {
    this.name = eventCategory.name
}

/**
 * EVENTS
 */
@Entity(
    tableName = "tennis_events",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseTennisEventCategory::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("category")),
        ForeignKey(
            entity = DatabaseCountry::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("country"))]
)
data class DatabaseTennisEvent constructor(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val association: String,
    var name: String,
    @ColumnInfo(name = "date_from")
    var dateFrom: Date,
    @ColumnInfo(name = "date_to")
    var dateTo: Date,
    val singles: Int?,
    val doubles: Int?,
    val indoor: Boolean?,
    val surface: String?,
    val city: String,
    val filter: Boolean,
    val list: Boolean,
    val country: Int,
    val category: Int
    )

@JvmName("databaseTennisEventAsDomainModel")
fun List<DatabaseTennisEvent>.asDomainModel(): List<Event> {
    return map {
        Event(
            id = it.id,
            name = it.name,
            dateFrom = it.dateFrom,
            dateTo = it.dateTo,
            category = it.category,
            filter = it.filter,
            list = it.list,
            city = it.city,
            county = it.country)
    }
}

@JvmName("databaseTennisEventUpdate")
fun DatabaseTennisEvent.update(event: DatabaseTennisEvent) {
    this.name = event.name
    this.dateFrom = event.dateFrom
    this.dateTo = event.dateTo
}

/**
 * FILTERED EVENTS
 */
data class DatabaseTennisFilteredEvent (
    @Relation(parentColumn = "id", entityColumn = "event_id")
    val entries: List<DatabaseTennisEntry>,
    @Embedded
    val event: DatabaseTennisEvent,
    @Embedded(prefix = "category_")
    val category: DatabaseTennisEventCategory,
    @Embedded(prefix = "country_")
    val country: DatabaseCountry
)

@JvmName("databaseTennisFilteredEventAsDomainModel")
fun List<DatabaseTennisFilteredEvent>.asCalendarListItems(): List<CalendarListItem> {
    return map {
        CalendarListItem(
            id = it.event.id,
            sport = Sport.TENNIS,
            name = it.event.name,
            dateFrom = it.event.dateFrom,
            dateTo = it.event.dateTo,
            category = it.category.name,
            details = mutableMapOf(
                "city" to it.event.city,
                "surface" to it.event.surface,
                "indoor" to it.event.indoor
            ),
            athletes = emptyList(),
            entries = emptyList(),
            country = it.country.asDomainModel()
        )
    }
}

/**
 * ENTRIES
 */
@Entity(
    tableName = "tennis_entries",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseTennisEvent::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("event_id"),
            onDelete = CASCADE),
        ForeignKey(
            entity = DatabaseTennisAthlete::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("athlete_id"),
            onDelete = CASCADE)],
    primaryKeys = [ "event_id", "athlete_id" ])
data class DatabaseTennisEntry (
    @ColumnInfo(name = "event_id")
    val eventId: Int,
    @ColumnInfo(name = "athlete_id")
    val athleteId: Int
)