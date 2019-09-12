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
 * ATHLETES
 */
@Entity(tableName = "cycling_athletes")
data class DatabaseCyclingAthlete constructor(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    var name: String,
    var gender: String,
    var filter: Boolean,
    var nationality: Int)

data class DatabaseCyclingAthleteWithCountry constructor(
    val id: Int,
    val name: String,
    val gender: String,
    val filter: Boolean,
    @Embedded(prefix = "country_")
    val country: DatabaseCountry
)

@JvmName("databaseCyclingAthleteWithCountryAsDomainModel")
fun List<DatabaseCyclingAthleteWithCountry>.asDomainModel(): List<Athlete> {
    return map {
        Athlete(id = it.id, name = it.name, gender = it.gender, filter = it.filter, nationality = it.country.asDomainModel())
    }
}

@JvmName("databaseCyclingAthleteUpdate")
fun DatabaseCyclingAthlete.update(athlete: DatabaseCyclingAthlete) {
    this.name = athlete.name
    this.gender = athlete.gender
    this.nationality = athlete.nationality
}

/**
 * EVENTS / CATEGORIES
 */
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
    var filter: Boolean,
    @ColumnInfo(name = "main_category_id")
    var mainCategory: Int?
)

@JvmName("databaseCyclingEventCategoryAsDomainModel")
fun List<DatabaseCyclingEventCategory>.asDomainModel(): List<EventCategory> {
    return map {
        EventCategory(id = it.id, name = it.name, filter = it.filter, mainCategory = it.mainCategory)
    }
}

@JvmName("databaseCyclingEventCategoryUpdate")
fun DatabaseCyclingEventCategory.update(eventCategory: DatabaseCyclingEventCategory) {
    this.name = eventCategory.name
    this.mainCategory = eventCategory.mainCategory
}

/**
 * EVENTS
 */
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
data class DatabaseCyclingEvent constructor(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    var name: String,
    @ColumnInfo(name = "date_from")
    var dateFrom: Date,
    @ColumnInfo(name = "date_to")
    var dateTo: Date,
    var filter: Boolean,
    var list: Boolean,
    var category: Int,
    var country: Int
)

@JvmName("databaseCyclingEventToDomainModel")
fun List<DatabaseCyclingEvent>.asDomainModel(): List<Event> {
    return map {
        Event(
            id = it.id,
            name = it.name,
            dateFrom = it.dateFrom,
            dateTo = it.dateTo,
            category = it.category,
            filter = it.filter,
            list = it.list,
            city = null,
            county = it.country)
    }
}

@JvmName("databaseCyclingEventUpdate")
fun DatabaseCyclingEvent.update(event: DatabaseCyclingEvent) {
    this.name = event.name
    this.dateFrom = event.dateFrom
    this.dateTo = event.dateTo
    this.country = event.country
    this.category = event.category
}

/**
 * FILTERED EVENTS
 */
data class DatabaseCyclingFilteredEvent (
    val entries: List<Int>?,
    @Embedded
    val event: DatabaseCyclingEvent,
    @Embedded(prefix = "category_")
    val category: DatabaseCyclingEventCategory,
    @Embedded(prefix = "country_")
    val country: DatabaseCountry
)

@JvmName("databaseCyclingFilteredEventAsDomainModel")
fun List<DatabaseCyclingFilteredEvent>.asCalendarListItems(): List<CalendarListItem> {
    return map {
        CalendarListItem(
            id = it.event.id,
            sport = Sport.CYCLING,
            name = it.event.name,
            dateFrom = it.event.dateFrom,
            dateTo = it.event.dateTo,
            category = it.category.name,
            athletes = emptyList(),
            entries = emptyList(),
            country = it.country.asDomainModel()
        )
    }
}