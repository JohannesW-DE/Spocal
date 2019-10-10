package dev.weinsheimer.sportscalendar.database.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import dev.weinsheimer.sportscalendar.domain.Athlete
import dev.weinsheimer.sportscalendar.domain.CalendarListItem
import dev.weinsheimer.sportscalendar.domain.Event
import dev.weinsheimer.sportscalendar.domain.EventCategory
import dev.weinsheimer.sportscalendar.util.Sport
import java.util.*

/**
 * Models
 */

@Entity(tableName = "tennis_athletes",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseCountry::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("nationality"),
            onDelete = CASCADE)])
data class DatabaseTennisAthlete(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    var name: String,
    var gender: String,
    val filter: Boolean,
    @ColumnInfo(index = true)
    var nationality: Int
)

@Entity(tableName = "tennis_event_categories")
data class DatabaseTennisEventCategory(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val association: String?,
    var name: String,
    val filter: Boolean
)

@Entity(
    tableName = "tennis_events",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseTennisEventCategory::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("category"),
            onDelete = CASCADE),
        ForeignKey(
            entity = DatabaseCountry::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("country"),
            onDelete = CASCADE)]
)
data class DatabaseTennisEvent(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    var association: String,
    var name: String,
    @ColumnInfo(name = "date_from")
    var dateFrom: Date,
    @ColumnInfo(name = "date_to")
    var dateTo: Date,
    var singles: Int?,
    var doubles: Int?,
    var indoor: Boolean?,
    var surface: String?,
    var city: String,
    var filter: Boolean,
    var list: Boolean,
    @ColumnInfo(index = true)
    var country: Int,
    @ColumnInfo(index = true)
    var category: Int
)

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
    @ColumnInfo(name = "event_id", index = true)
    val eventId: Int,
    @ColumnInfo(name = "athlete_id", index = true)
    val athleteId: Int
)

/**
 * PoJos
 */

data class DatabaseTennisEntryWithAthletes(
    @Embedded
    var entry: DatabaseTennisEntry,
    @Relation(parentColumn = "athlete_id", entityColumn = "id", entity = DatabaseTennisAthlete::class)
    val athletes: List<DatabaseTennisAthlete>
)

data class DatabaseTennisEventWithAthletes(
    @Embedded
    val event: DatabaseTennisEvent,
    @Embedded(prefix = "category_")
    val category: DatabaseTennisEventCategory,
    @Embedded(prefix = "country_")
    val country: DatabaseCountry,
    @Relation(parentColumn = "id", entityColumn = "event_id", entity = DatabaseTennisEntry::class)
    val entries: List<DatabaseTennisEntryWithAthletes>
)

/**
 * Extensions
 */

@JvmName("databaseTennisAthleteAsDomainModel")
fun List<DatabaseTennisAthlete>.asDomainModel(): List<Athlete> {
    return map {
        Athlete(id = it.id, name = it.name, filter = it.filter)
    }
}

@JvmName("databaseTennisAthleteUpdate")
fun DatabaseTennisAthlete.update(athlete: DatabaseTennisAthlete) {
    apply {
        name = athlete.name
        gender = athlete.gender
        nationality = athlete.nationality
    }
}

@JvmName("databaseTennisEventCategoryAsDomainModel")
fun List<DatabaseTennisEventCategory>.asDomainModel(): List<EventCategory> {
    return map {
        var name = it.name
        it.association?.let { association ->
            name = association.toUpperCase(Locale.US) + " - " + name
        }
        EventCategory(id = it.id, name = name, filter = it.filter, mainCategory = null)
    }
}

@JvmName("databaseTennisEventCategoryUpdate")
fun DatabaseTennisEventCategory.update(eventCategory: DatabaseTennisEventCategory) {
    this.name = eventCategory.name
}


@JvmName("databaseTennisEventAsDomainModel")
fun List<DatabaseTennisEvent>.asDomainModel(): List<Event> {
    return map {
        Event(id = it.id, name = it.name,filter = it.filter, category = it.category)
    }
}

@JvmName("databaseTennisEventUpdate")
fun DatabaseTennisEvent.update(event: DatabaseTennisEvent) {
    apply {
        name = event.name
        dateFrom = event.dateFrom
        dateTo = event.dateTo
        singles = event.singles
        doubles = event.doubles
        indoor = event.indoor
        surface = event.surface
        city = event.city
        country = event.country
        category = event.category
    }
}

@JvmName("databaseTennisEntryWithAthletesAsDomainModel")
fun List<DatabaseTennisEntryWithAthletes>.asDomainModel(): List<Athlete> {
    return this.map { it.athletes }.flatten().asDomainModel()
}

@JvmName("databaseTennisEventWithAthletesAsCalendarListItems")
fun List<DatabaseTennisEventWithAthletes>.asCalendarListItems(): List<CalendarListItem> {
    return map {
        CalendarListItem(
            id = it.event.id,
            sport = Sport.TENNIS,
            name = it.event.name,
            dateFrom = it.event.dateFrom,
            dateTo = it.event.dateTo,
            category = it.category.name,
            athletes = it.entries.asDomainModel(),
            details = mutableMapOf(
                "city" to it.event.city,
                "surface" to it.event.surface,
                "indoor" to it.event.indoor
            ),
            country = it.country.asDomainModel()
        )
    }
}
