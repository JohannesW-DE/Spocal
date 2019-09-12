package dev.weinsheimer.sportscalendar.database.model

import androidx.room.*
import dev.weinsheimer.sportscalendar.domain.Country

/**
 * Models
 */

@Entity(tableName = "countries")
data class DatabaseCountry(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    var name: String,
    var alphatwo: String?
)

/**
 * Extensions
 */

@JvmName("singleDatabaseCountryAsDomainModel")
fun DatabaseCountry.asDomainModel(): Country {
    return Country(id = this.id, name = this.name, alphatwo = this.alphatwo)
}

@JvmName("databaseCountryAsDomainModel")
fun List<DatabaseCountry>.asDomainModel(): List<Country> {
    return map {
        it.asDomainModel()
    }
}

