package dev.weinsheimer.sportscalendar.database.model

import androidx.room.*
import dev.weinsheimer.sportscalendar.domain.Country
import dev.weinsheimer.sportscalendar.domain.Filter
import dev.weinsheimer.sportscalendar.domain.FilterResult

/**
 * DatabaseCountry
 */
@Entity(tableName = "countries")
data class DatabaseCountry constructor(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    var name: String,
    var alphatwo: String?)

@JvmName("databaseCountryAsDomainModel")
fun List<DatabaseCountry>.asDomainModel(): List<Country> {
    return map {
        Country(
            id = it.id,
            name = it.name,
            alphatwo = it.alphatwo)
    }
}

@JvmName("singleDatabaseCountryAsDomainModel")
fun DatabaseCountry.asDomainModel(): Country {
    return Country(id = this.id, name = this.name, alphatwo = this.alphatwo)
}

/**
 * DatabaseFilter
 */
@Entity(
    tableName = "filters",
    indices = [Index(value = ["sport", "type", "value"], unique = true)]
)
data class DatabaseFilter constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val sport: String,
    val type: String,
    val value: Int)

@JvmName("databaseFilterAsDomainModel")
fun List<DatabaseFilter>.asDomainModel(): List<Filter> {
    return map { Filter(sport = it.sport, type = it.type, value = it.value) }
}

/**
 * DatabaseFilterResult
 */
@Entity(
    tableName = "filter_results",
    indices = [Index(value = ["sport", "event_id"], unique = true)]
)
data class DatabaseFilterResult constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val sport: String,
    @ColumnInfo(name = "event_id")
    val eventId: Int,
    val entries: List<Int>?,
    val visible: Boolean = true
)

@JvmName("databaseFilterResultAsDomainModel")
fun List<DatabaseFilterResult>.asDomainModel(): List<FilterResult> {
    return map {
        FilterResult(
            sport = it.sport,
            eventId = it.eventId,
            entries = it.entries)
    }
}

