package dev.weinsheimer.sportscalendar.database

import android.provider.ContactsContract
import java.util.*

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

import dev.weinsheimer.sportscalendar.domain.*

/**
 * ATHLETES
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
    override val id: Int,
    var name: String,
    var gender: String,
    var filter: Boolean,
    var nationality: Int
) : DatabaseAthlete

@JvmName("databaseBadmintonAthleteUpdate")
fun DatabaseBadmintonAthlete.update(athlete: DatabaseBadmintonAthlete) {
    this.name = athlete.name
    this.gender = athlete.gender
    this.nationality = athlete.nationality
}

data class DatabaseBadmintonAthleteWithCountry constructor(
    val id: Int,
    val name: String,
    val gender: String,
    val filter: Boolean,
    @Embedded(prefix = "country_")
    val country: DatabaseCountry
)

@JvmName("databaseBadmintonAthleteWithCountryAsDomainModel")
fun List<DatabaseBadmintonAthleteWithCountry>.asDomainModel(): List<Athlete> {
    return map {
        Athlete(id = it.id, name = it.name, gender = it.gender, filter = it.filter, nationality = it.country.asDomainModel())
    }
}

/**
 * EVENTS
 */

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
data class DatabaseBadmintonEvent constructor(
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

@JvmName("databaseBadmintonEventAsDomainModel")
fun List<DatabaseBadmintonEvent>.asDomainModel(): List<Event> {
    return map {
        Event(
            id = it.id,
            name = it.name,
            dateFrom = it.dateFrom,
            dateTo = it.dateTo,
            city = it.city,
            filter = it.filter,
            list = it.list,
            county = it.country,
            category = it.category)
    }
}

@JvmName("databaseBadmintonEventUpdate")
fun DatabaseBadmintonEvent.update(event: DatabaseBadmintonEvent) {
    this.name = event.name
    this.dateFrom = event.dateFrom
    this.city = event.city
    this.country = event.country
    this.category = event.category
}

/**
 * EVENT CATEGORIES
 */

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

data class DatabaseBadmintonFilteredEvent (
    @Relation(parentColumn = "id", entityColumn = "event_id")
    val entries: List<DatabaseBadmintonEntry>,
    @Embedded
    val event: DatabaseBadmintonEvent,
    @Embedded(prefix = "category_")
    val category: DatabaseBadmintonEventCategory,
    @Embedded(prefix = "country_")
    val country: DatabaseCountry
)

@JvmName("databaseBadmintonFilteredEventAsDomainModel")
fun List<DatabaseBadmintonFilteredEvent>.asCalendarListItems(): List<CalendarListItem> {
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
        CalendarListItem(
            sport = "badminton",
            eventId = it.event.id,
            name = it.event.name,
            category = category,
            dateFrom = it.event.dateFrom,
            dateTo = it.event.dateTo,
            entries = it.entries.map { entry -> entry.athleteId },
            details = mutableMapOf("city" to it.event.city),
            country = it.country.asDomainModel()
        )
    }
}