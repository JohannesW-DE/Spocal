package dev.weinsheimer.sportscalendar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.squareup.moshi.JsonDataException
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
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.net.SocketTimeoutException

class CountryRepository: KoinComponent {
    private val database: SpocalDB by inject()
    private val retrofitService: ApiService by inject()

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
                val response = retrofitService.getCountries()
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