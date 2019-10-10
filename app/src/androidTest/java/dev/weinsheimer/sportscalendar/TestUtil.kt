package dev.weinsheimer.sportscalendar

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.weinsheimer.sportscalendar.database.SpocalDB
import dev.weinsheimer.sportscalendar.network.NetworkCountryContainer
import dev.weinsheimer.sportscalendar.network.asDatabaseModel
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class TestUtil : KoinComponent {
    val database: SpocalDB by inject()
    private val context: Context by inject()

    init {
        populateCountries()
    }

    fun read(file: String) : String {
        return context.assets.open(file).bufferedReader().use{
            it.readText()
        }
    }

    private fun populateCountries() {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val json = read("countries_seed.json")
        moshi.adapter(NetworkCountryContainer::class.java).fromJson(json)?.let {
            database.countryDao.insertCountries(*it.asDatabaseModel())
        }
    }

    abstract fun populateAthletes()
    abstract fun populateEventCategories()
    abstract fun populateEvents()

    abstract fun modifyEntries()

    fun seed() {
        populateAthletes()
        populateEventCategories()
        populateEvents()
    }

    abstract fun modify()
}