package dev.weinsheimer.sportscalendar.repository

import android.view.LayoutInflater
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.google.gson.Gson
import com.squareup.moshi.JsonDataException
import dev.weinsheimer.sportscalendar.database.*
import dev.weinsheimer.sportscalendar.database.model.asCalendarListItems
import dev.weinsheimer.sportscalendar.database.model.asDomainModel
import dev.weinsheimer.sportscalendar.database.model.update
import dev.weinsheimer.sportscalendar.domain.*
import dev.weinsheimer.sportscalendar.network.Api
import dev.weinsheimer.sportscalendar.network.ApiService
import dev.weinsheimer.sportscalendar.network.asDatabaseModel
import dev.weinsheimer.sportscalendar.util.RefreshException
import dev.weinsheimer.sportscalendar.util.RefreshExceptionType
import dev.weinsheimer.sportscalendar.util.refresh
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.SocketTimeoutException

class CyclingRepository(val database: SpocalDB, val retrofitService: ApiService): BaseRepository(database) {
    override var sport = "badminton"
    override var dao: BaseDao = database.cyclingDao

    /**
     * DAO calls
     */
    override var athletes =
        Transformations.map(database.cyclingDao.getAthletesWithCountry()) {
            it.asDomainModel()
        }

    override var events =
        Transformations.map(database.cyclingDao.getEvents()) {
            it.asDomainModel()
        }

    override var eventCategories =
        Transformations.map(database.cyclingDao.getEventCategories()) {
            it.asDomainModel()
        }

    override var mainEventCategories =
        Transformations.map(database.cyclingDao.getEventCategories()) {
            it.asDomainModel()
        }

    override var calendarItems =
        Transformations.map(database.cyclingDao.getFilteredEvents()) {
            it.asCalendarListItems()
        }

    /**
     * API + DAO calls
     */
    override suspend fun refreshAthletesFunction() {
        val response = retrofitService.getCyclingAthletes()
        if (response.code() == 200) {
            response.body()?.let { container ->
                // objects in the database but not in the container
                val currents = database.cyclingDao.getCurrentAthletes()
                val futures = container.asDatabaseModel()
                val deletes = currents.filter { current ->
                    !futures.map { it.id }.contains(current.id)
                }
                currents.forEach { current ->
                    futures.find { it.id == current.id }?.let { future ->
                        current.update(future)
                    }
                }
                database.cyclingDao.deleteAthletes(*deletes.toTypedArray())
                database.cyclingDao.insertAthletes(*container.asDatabaseModel())
                database.cyclingDao.updateAthletes(*currents.toTypedArray())
            }
        } else {
            throw RefreshException(RefreshExceptionType.CODE)
        }
    }

    override suspend fun refreshEventCategoriesFunction() {
        val response = retrofitService.getCyclingEventCategories()
        if (response.code() == 200) {
            response.body()?.let { container ->
                // objects in the database but not in the container
                val currents = database.cyclingDao.getCurrentEventCategories()
                val futures = container.asDatabaseModel()
                val deletes = currents.filter { current ->
                    !futures.map { it.id }.contains(current.id)
                }
                // ignore while updating: filter
                currents.forEach { current ->
                    futures.find { it.id == current.id }?.let { future ->
                        current.update(future)
                    }
                }
                database.cyclingDao.deleteEventCategories(*deletes.toTypedArray())
                database.cyclingDao.insertEventCategories(*container.asDatabaseModel())
                database.cyclingDao.updateEventCategories(*currents.toTypedArray())
            }
        } else {
            throw RefreshException(RefreshExceptionType.CODE)
        }
    }

    override suspend fun refreshEventsFunction() {
        val response = retrofitService.getCyclingEvents()
        if (response.code() == 200) {
            response.body()?.let { container ->
                // objects in the database but not in the container
                val currents = database.cyclingDao.getCurrentEvents()
                val futures = container.asDatabaseModel()
                val deletes = currents.filter { current ->
                    !futures.map { it.id }.contains(current.id)
                }
                // ignore while updating: filter
                currents.forEach { current ->
                    futures.find { it.id == current.id }?.let { future ->
                        current.update(future)
                    }
                }
                database.cyclingDao.deleteEvents(*deletes.toTypedArray())
                database.cyclingDao.insertEvents(*container.asDatabaseModel())
                database.cyclingDao.updateEvents(*currents.toTypedArray())
            }
        } else {
            throw RefreshException(RefreshExceptionType.CODE)
        }
    }

    override suspend fun updateEventsFunction() {
        val response = retrofitService.getCyclingFilterResults(createNetworkRequest())
        if (response.code() == 200) {
            response.body()?.let { container ->
                dao.resetEventListStatus()
                dao.clearEntries()
                container.events.forEach { event ->
                    dao.changeEventListStatus(true, event.id)
                }
            }
        } else {
            throw RefreshException(RefreshExceptionType.CODE)
        }
    }
}