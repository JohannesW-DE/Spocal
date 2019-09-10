package dev.weinsheimer.sportscalendar.repository

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.squareup.moshi.JsonDataException
import dev.weinsheimer.sportscalendar.R
import dev.weinsheimer.sportscalendar.database.DatabaseFilter
import dev.weinsheimer.sportscalendar.database.SpocalDB
import dev.weinsheimer.sportscalendar.database.asDomainModel
import dev.weinsheimer.sportscalendar.domain.Country
import dev.weinsheimer.sportscalendar.domain.Filter
import dev.weinsheimer.sportscalendar.network.Api
import dev.weinsheimer.sportscalendar.network.asDatabaseModel
import dev.weinsheimer.sportscalendar.util.RefreshException
import dev.weinsheimer.sportscalendar.util.RefreshExceptionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.SocketTimeoutException

class CommonRepository(private val database: SpocalDB) {
    /**
     * DAO calls
     */
    val countries: LiveData<List<Country>> =
        Transformations.map(database.commonDao.getCountries()) {
            it.asDomainModel()
        }

    val filterCount: LiveData<Int> =
        Transformations.map(database.commonDao.getFilters()) {
            it.asDomainModel().size
        }

    suspend fun ping() {
        withContext(Dispatchers.IO) {
            try {
                val response = Api.retrofitService.getPing()
                if (response.code() != 200) {
                    throw RefreshException(RefreshExceptionType.CODE)
                }
              } catch (e: SocketTimeoutException) {
                throw RefreshException(RefreshExceptionType.CONNECTION)
            } catch (e: JsonDataException) {
                throw RefreshException(RefreshExceptionType.FORMAT)
            }
        }
    }

    /**
     * API + DAO calls
     */
    suspend fun refreshCountries() {
        withContext(Dispatchers.IO) {
            try {
                val response = Api.retrofitService.getCountries()
                if (response.code() == 200) {
                    response.body()?.let { container ->
                        val currentCountries = database.commonDao.getCurrentCountries()
                        val deletes = currentCountries.filter { currentCountry ->
                            !container.asDatabaseModel().map { it.id }.contains(currentCountry.id)
                        }
                        val inserts = container.asDatabaseModel().filter { newCountry ->
                            !currentCountries.map { it.id }.contains(newCountry.id)
                        }
                        database.commonDao.deleteCountries(*deletes.toTypedArray())
                        database.commonDao.insertCountries(*inserts.toTypedArray())
                    }
                } else {
                    throw RefreshException(RefreshExceptionType.CODE)
                }
            } catch (e: SocketTimeoutException) {
                throw RefreshException(RefreshExceptionType.CONNECTION)
            } catch (e: JsonDataException) {
                throw RefreshException(RefreshExceptionType.FORMAT)
            }
        }
    }

    suspend fun getFilters(sport: String): LiveData<List<DatabaseFilter>> {
        return withContext(Dispatchers.IO) {
            database.commonDao.getFilters(sport)
        }
    }


    suspend fun getCurrentFilters(sport: String): List<Filter> {
        return withContext(Dispatchers.IO) {
            database.commonDao.getCurrentFilters(sport).asDomainModel()
        }
    }

    suspend fun insertFilters(filters: List<DatabaseFilter>) {
        withContext(Dispatchers.IO) {
            database.commonDao.insertFilters(*filters.toTypedArray())
        }
    }

    suspend fun clearFilters(sport: String) {
        withContext(Dispatchers.IO) {
            database.commonDao.deleteFilters(sport)
        }
    }
    suspend fun clearFilters() {
        withContext(Dispatchers.IO) {
            database.commonDao.deleteFilters()
        }
    }

    suspend fun hideFilterResult(sport: String, eventId: Int) {
        withContext(Dispatchers.IO) {
            database.commonDao.updateFilterResultVisibility(sport, eventId)
        }
    }
}