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
    @ColumnInfo(name = "main_category_id")
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
    var country: Int,
    var category: Int
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
        val name = when(it.name) {
            "Individual Tournaments" -> "BWF World Championships"
            "Team Tournaments" -> "BWF World Mixed Team Championships"
            "HSBC BWF World Tour Finals" -> "World Tour Finals"
            "HSBC BWF World Tour Super 1000" -> "Super 1000"
            "HSBC BWF World Tour Super 750" -> "Super 750"
            "HSBC BWF World Tour Super 500" -> "Super 500"
            "HSBC BWF World Tour Super 300" -> "Super 300"
            "BWF Tour Super 100" -> "Super 100"
            else -> it.name
        }
        EventCategory(id = it.id, name = name, filter = it.filter, mainCategory = it.mainCategory)
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




@JvmName("databaseBadmintonEventCategoryUpdate")
fun DatabaseBadmintonEventCategory.update(eventCategory: DatabaseBadmintonEventCategory) {
    this.name = eventCategory.name
    this.mainCategory = eventCategory.mainCategory
}

/**
 * ENTRIES
 */
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
    @ColumnInfo(name = "event_id")
    val eventId: Int,
    @ColumnInfo(name = "athlete_id")
    val athleteId: Int
)

/**
 * FILTERED EVENTS
 */

data class DatabaseBadmintonEntryWithAthletes(
    @Embedded
    var entry: DatabaseBadmintonEntry,
    @Relation(parentColumn = "athlete_id", entityColumn = "id", entity = DatabaseBadmintonAthlete::class)
    val athletes: List<DatabaseBadmintonAthlete>
)

@JvmName("databaseBadmintonEntryWithAthletesAsDomainModel")
fun List<DatabaseBadmintonEntryWithAthletes>.asDomainModel(): List<Athlete> {
    return this.map { it.athletes }.flatten().asDomainModel()
}

data class DatabaseBadmintonEventWithAthletes(
    @Embedded
    val event: DatabaseBadmintonEvent,
    @Embedded(prefix = "category_")
    val category: DatabaseBadmintonEventCategory,
    @Relation(parentColumn = "id", entityColumn = "event_id", entity = DatabaseBadmintonEntry::class)
    val entries: List<DatabaseBadmintonEntryWithAthletes>
)

@JvmName("databaseBadmintonFilteredEventAsDomainModel")
fun List<DatabaseBadmintonEventWithAthletes>.asCalendarListItems(): List<CalendarListItem> {
    return map {
        val category = when(it.category.name) {
            "Individual Tournaments" -> "BWF World Championships"
            "Team Tournaments" -> "BWF World Mixed Team Championships"
            "HSBC BWF World Tour Finals" -> "World Tour Finals"
            "HSBC BWF World Tour Super 1000" -> "Super 1000"
            "HSBC BWF World Tour Super 750" -> "Super 750"
            "HSBC BWF World Tour Super 500" -> "Super 500"
            "HSBC BWF World Tour Super 300" -> "Super 300"
            "BWF Tour Super 100" -> "Super 100"
            else -> it.category.name
        }
        println("@@calendarlistitem")
        println(it.event.dateFrom)
        println(it.event.dateTo)
        CalendarListItem(
            id = it.event.id,
            sport = Sport.BADMINTON,
            name = it.event.name,
            dateFrom = it.event.dateFrom,
            dateTo = it.event.dateTo,
            category = category,
            details = mutableMapOf(
                "city" to it.event.city
            ),
            athletes = it.entries.asDomainModel(),
            entries = emptyList(),
            country = Country(1,"Country", "XD")
        )
    }
}