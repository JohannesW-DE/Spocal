package dev.weinsheimer.sportscalendar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import dev.weinsheimer.sportscalendar.database.SpocalDB
import dev.weinsheimer.sportscalendar.database.model.asDomainModel
import dev.weinsheimer.sportscalendar.domain.Country
import dev.weinsheimer.sportscalendar.network.ApiService
import dev.weinsheimer.sportscalendar.network.asDatabaseModel
import dev.weinsheimer.sportscalendar.util.RefreshException
import dev.weinsheimer.sportscalendar.util.RefreshExceptionType
import dev.weinsheimer.sportscalendar.util.refresh
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountryRepository @Inject constructor(
    private val database: SpocalDB,
    private val retrofitService: ApiService
) {
    /**
     * DAO calls
     */
    val countries: LiveData<List<Country>> =
        Transformations.map(database.countryDao.getCountries()) {
            it.asDomainModel()
        }

    /**
     * API + DAO calls
     */
    suspend fun refreshCountries() {
        refresh {
            withContext(Dispatchers.IO) {
                println("testing")
                val response = retrofitService.getCountries()
                println(response.code())
                if (response.code() == 200) {
                    println("COUNTRYCONTAINER")
                    response.body()?.let { container ->
                        val currentCountries = database.countryDao.getCurrentCountries()
                        val deletes = currentCountries.filter { currentCountry ->
                            !container.asDatabaseModel().map { it.id }.contains(currentCountry.id)
                        }
                        val inserts = container.asDatabaseModel().filter { newCountry ->
                            !currentCountries.map { it.id }.contains(newCountry.id)
                        }
                        database.countryDao.deleteCountries(*deletes.toTypedArray())
                        database.countryDao.insertCountries(*inserts.toTypedArray())
                    }
                } else {
                    throw RefreshException(RefreshExceptionType.CODE)
                }
            }
        }
    }
}