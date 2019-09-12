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
@Entity(tableName = "cycling_athletes",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseCountry::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("nationality"),
            onDelete = CASCADE)])
data class DatabaseCyclingAthlete(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    var name: String,
    var gender: String,
    val filter: Boolean,
    @ColumnInfo(index = true)
    var nationality: Int
)

@Entity(
    tableName = "cycling_event_categories",
    foreignKeys = [ForeignKey(
        entity = DatabaseCyclingEventCategory::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("main_category_id"),
        onDelete = CASCADE)]
)
data class DatabaseCyclingEventCategory constructor(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    var name: String,
    val filter: Boolean,
    @ColumnInfo(name = "main_category_id", index = true)
    var mainCategory: Int?
)

@Entity(
    tableName = "cycling_events",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseCyclingEventCategory::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("category")),
        ForeignKey(
            entity = DatabaseCountry::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("country"))]
)
data class DatabaseCyclingEvent(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    var name: String,
    @ColumnInfo(name = "date_from")
    var dateFrom: Date,
    @ColumnInfo(name = "date_to")
    var dateTo: Date,
    val filter: Boolean,
    val list: Boolean,
    @ColumnInfo(index = true)
    var category: Int,
    @ColumnInfo(index = true)
    var country: Int
)

@Entity(
    tableName = "cycling_entries",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseCyclingEvent::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("event_id"),
            onDelete = CASCADE),
        ForeignKey(
            entity = DatabaseCyclingAthlete::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("athlete_id"),
            onDelete = CASCADE)],
    primaryKeys = [ "event_id", "athlete_id" ])
data class DatabaseCyclingEntry (
    @ColumnInfo(name = "event_id", index = true)
    val eventId: Int,
    @ColumnInfo(name = "athlete_id", index = true)
    val athleteId: Int
)

/**
 * PoJos
 */

data class DatabaseCyclingEntryWithAthletes(
    @Embedded
    var entry: DatabaseCyclingEntry,
    @Relation(parentColumn = "athlete_id", entityColumn = "id", entity = DatabaseCyclingAthlete::class)
    val athletes: List<DatabaseCyclingAthlete>
)

data class DatabaseCyclingEventWithAthletes(
    @Embedded
    val event: DatabaseCyclingEvent,
    @Embedded(prefix = "category_")
    val category: DatabaseCyclingEventCategory,
    @Embedded(prefix = "country_")
    val country: DatabaseCountry,
    @Relation(parentColumn = "id", entityColumn = "event_id", entity = DatabaseCyclingEntry::class)
    val entries: List<DatabaseCyclingEntryWithAthletes>
)

/**
 * Extensions
 */

@JvmName("databaseCyclingAthleteAsDomainModel")
fun List<DatabaseCyclingAthlete>.asDomainModel(): List<Athlete> {
    return map {
        Athlete(id = it.id, name = it.name, filter = it.filter)
    }
}

@JvmName("databaseCyclingAthleteUpdate")
fun DatabaseCyclingAthlete.update(athlete: DatabaseCyclingAthlete) {
    apply {
        name = athlete.name
        gender = athlete.gender
        nationality = athlete.nationality
    }
}

@JvmName("databaseCyclingEventCategoryAsDomainModel")
fun List<DatabaseCyclingEventCategory>.asDomainModel(): List<EventCategory> {
    return map {
        EventCategory(id = it.id, name = it.name, filter = it.filter, mainCategory = it.mainCategory)
    }
}

@JvmName("databaseCyclingEventCategoryUpdate")
fun DatabaseCyclingEventCategory.update(eventCategory: DatabaseCyclingEventCategory) {
    apply {
        name = eventCategory.name
        mainCategory = eventCategory.mainCategory
    }
}

@JvmName("databaseCyclingEventToDomainModel")
fun List<DatabaseCyclingEvent>.asDomainModel(): List<Event> {
    return map {
        Event(id = it.id, name = it.name,filter = it.filter, category = it.category)
    }
}

@JvmName("databaseCyclingEventUpdate")
fun DatabaseCyclingEvent.update(event: DatabaseCyclingEvent) {
    apply {
        name = event.name
        dateFrom = event.dateFrom
        dateTo = event.dateTo
        country = event.country
        category = event.category
    }
}

@JvmName("databaseCyclingEntryWithAthletesAsDomainModel")
fun List<DatabaseCyclingEntryWithAthletes>.asDomainModel(): List<Athlete> {
    return this.map { it.athletes }.flatten().asDomainModel()
}

@JvmName("databaseCyclingEventWithAthletesAsCalendarListItems")
fun List<DatabaseCyclingEventWithAthletes>.asCalendarListItems(): List<CalendarListItem> {
    return map {
        CalendarListItem(
            id = it.event.id,
            sport = Sport.CYCLING,
            name = it.event.name,
            dateFrom = it.event.dateFrom,
            dateTo = it.event.dateTo,
            category = it.category.name,
            athletes = it.entries.asDomainModel(),
            country = it.country.asDomainModel()
        )
    }
}