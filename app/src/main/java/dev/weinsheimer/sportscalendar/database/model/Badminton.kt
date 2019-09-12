package dev.weinsheimer.sportscalendar.database.model

import java.util.*

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

import dev.weinsheimer.sportscalendar.domain.*
import dev.weinsheimer.sportscalendar.util.Sport

/**
 * Models
 */

@Entity(
    tableName = "badminton_athletes",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseCountry::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("nationality"),
            onDelete = CASCADE)]
)
data class DatabaseBadmintonAthlete(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    var name: String,
    var gender: String,
    var filter: Boolean,
    @ColumnInfo(index = true)
    var nationality: Int
)

@Entity(
    tableName = "badminton_event_categories",
    foreignKeys = [ForeignKey(
        entity = DatabaseBadmintonEventCategory::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("main_category_id"),
        onDelete = CASCADE)]
)
data class DatabaseBadmintonEventCategory constructor(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    var name: String,
    var filter: Boolean,
    @ColumnInfo(name = "main_category_id", index = true)
    var mainCategory: Int?
)

@Entity(
    tableName = "badminton_events",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseBadmintonEventCategory::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("category"),
            onDelete = CASCADE),
        ForeignKey(
            entity = DatabaseCountry::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("country"),
            onDelete = CASCADE)]
)
data class DatabaseBadmintonEvent(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    var name: String,
    @ColumnInfo(name = "date_from")
    var dateFrom: Date,
    @ColumnInfo(name = "date_to")
    var dateTo: Date,
    var city: String,
    var filter: Boolean,
    var list: Boolean,
    @ColumnInfo(index = true)
    var country: Int,
    @ColumnInfo(index = true)
    var category: Int
)

@Entity(
    tableName = "badminton_entries",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseBadmintonEvent::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("event_id"),
            onDelete = CASCADE),
        ForeignKey(
            entity = DatabaseBadmintonAthlete::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("athlete_id"),
            onDelete = CASCADE)],
    primaryKeys = [ "event_id", "athlete_id" ])
data class DatabaseBadmintonEntry (
    @ColumnInfo(name = "event_id", index = true)
    val eventId: Int,
    @ColumnInfo(name = "athlete_id", index = true)
    val athleteId: Int
)

/**
 * PoJos
 */

data class DatabaseBadmintonEntryWithAthletes(
    @Embedded
    var entry: DatabaseBadmintonEntry,
    @Relation(parentColumn = "athlete_id", entityColumn = "id", entity = DatabaseBadmintonAthlete::class)
    val athletes: List<DatabaseBadmintonAthlete>
)

data class DatabaseBadmintonEventWithAthletes(
    @Embedded
    val event: DatabaseBadmintonEvent,
    @Embedded(prefix = "category_")
    val category: DatabaseBadmintonEventCategory,
    @Embedded(prefix = "country_")
    val country: DatabaseCountry,
    @Relation(parentColumn = "id", entityColumn = "event_id", entity = DatabaseBadmintonEntry::class)
    val entries: List<DatabaseBadmintonEntryWithAthletes>
)

/**
 * Extensions
 */

@JvmName("databaseBadmintonAthleteAsDomainModel")
fun List<DatabaseBadmintonAthlete>.asDomainModel(): List<Athlete> {
    return map {
        Athlete(id = it.id, name = it.name, filter = it.filter)
    }
}

@JvmName("databaseBadmintonAthleteUpdate")
fun DatabaseBadmintonAthlete.update(athlete: DatabaseBadmintonAthlete) {
    apply {
        name = athlete.name
        gender = athlete.gender
        nationality = athlete.nationality
    }
}

@JvmName("databaseBadmintonEventCategoryAsDomainModel")
fun List<DatabaseBadmintonEventCategory>.asDomainModel(): List<EventCategory> {
    return map {
        EventCategory(id = it.id, name = it.name, filter = it.filter, mainCategory = it.mainCategory)
    }
}

@JvmName("databaseBadmintonEventCategoryUpdate")
fun DatabaseBadmintonEventCategory.update(eventCategory: DatabaseBadmintonEventCategory) {
    apply {
        name = eventCategory.name
        mainCategory = eventCategory.mainCategory
    }
}

@JvmName("databaseBadmintonEventAsDomainModel")
fun List<DatabaseBadmintonEvent>.asDomainModel(): List<Event> {
    return map {
        Event(id = it.id, name = it.name,filter = it.filter, category = it.category)
    }
}

@JvmName("databaseBadmintonEventUpdate")
fun DatabaseBadmintonEvent.update(event: DatabaseBadmintonEvent) {
    apply {
        name = event.name
        dateFrom = event.dateFrom
        dateTo = event.dateTo
        city = event.city
        country = event.country
        category = event.category
    }
}

@JvmName("databaseBadmintonEntryWithAthletesAsDomainModel")
fun List<DatabaseBadmintonEntryWithAthletes>.asDomainModel(): List<Athlete> {
    return this.map { it.athletes }.flatten().asDomainModel()
}

@JvmName("databaseBadmintonEventWithAthletesAsCalendarListItems")
fun List<DatabaseBadmintonEventWithAthletes>.asCalendarListItems(): List<CalendarListItem> {
    return map {
        CalendarListItem(
            id = it.event.id,
            sport = Sport.BADMINTON,
            name = it.event.name,
            dateFrom = it.event.dateFrom,
            dateTo = it.event.dateTo,
            category = it.category.name,
            athletes = it.entries.asDomainModel(),
            details = mutableMapOf(
                "city" to it.event.city
            ),
            country = it.country.asDomainModel()
        )
    }
}