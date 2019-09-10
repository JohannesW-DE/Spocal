package dev.weinsheimer.sportscalendar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.google.gson.Gson
import com.squareup.moshi.JsonDataException
import dev.weinsheimer.sportscalendar.database.*
import dev.weinsheimer.sportscalendar.domain.*
import dev.weinsheimer.sportscalendar.network.*
import dev.weinsheimer.sportscalendar.util.RefreshException
import dev.weinsheimer.sportscalendar.util.RefreshExceptionType
import dev.weinsheimer.sportscalendar.util.refresh
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.SocketTimeoutException

class TennisRepository(val database: SpocalDB, val retrofitService: ApiService): BaseRepository(database) {
    override var sport = "tennis"
    override var dao: BaseDao = database.tennisDao

    override var athletes =
        Transformations.map(database.tennisDao.getAthletesWithCountry()) {
            println(sport + " - athletes -> " + it.size)
            it.asDomainModel()
        }

    override var events =
        Transformations.map(database.tennisDao.getEvents()) {
            println(sport + " - events -> " + it.size)
            it.asDomainModel()
        }

    override var mainEventCategories =
        Transformations.map(database.tennisDao.getEventCategories()) {
            println(sport + " - mainEventCategories -> " + it.size)
            it.asDomainModel()
        }

    override var eventCategories =
        Transformations.map(database.tennisDao.getEventCategories()) {
            println(sport + " - eventCategories -> " + it.size)
            it.asDomainModel()
        }

    override var calendarItems =
        Transformations.map(database.tennisDao.getFilteredEvents()) {
            println(sport + " - _calendarItems_ -> " + it.size)
            it.asCalendarListItems()
        }

    override suspend fun refreshAthletesFunction() {
        val response = retrofitService.getTennisAthletes()
        if (response.code() == 200) {
            response.body()?.let { container ->
                // objects in the database but not in the container
                val currents = database.tennisDao.getCurrentAthletes()
                val futures = container.asDatabaseModel()
                val deletes = currents.filter { current ->
                    !futures.map { it.id }.contains(current.id)
                }
                currents.forEach { current ->
                    futures.find { it.id == current.id }?.let { future ->
                        current.update(future)
                    }
                }
                database.tennisDao.deleteAthletes(*deletes.toTypedArray())
                database.tennisDao.insertAthletes(*container.asDatabaseModel())
                database.tennisDao.updateAthletes(*currents.toTypedArray())
            }
        } else {
            throw RefreshException(RefreshExceptionType.CODE)
        }
    }

    override suspend fun refreshEventCategoriesFunction() {
        val response = retrofitService.getTennisEventCategories()
        if (response.code() == 200) {
            response.body()?.let { container ->
                // objects in the database but not in the container
                val currents = database.tennisDao.getCurrentEventCategories()
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
                database.tennisDao.deleteEventCategories(*deletes.toTypedArray())
                database.tennisDao.insertEventCategories(*container.asDatabaseModel())
                database.tennisDao.updateEventCategories(*currents.toTypedArray())
            }
        } else {
            throw RefreshException(RefreshExceptionType.CODE)
        }
    }

    override suspend fun refreshEventsFunction() {
        val response = retrofitService.getTennisEvents()
        if (response.code() == 200) {
            response.body()?.let { container ->
                // objects in the database but not in the container
                val currents = database.tennisDao.getCurrentEvents()
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
                database.tennisDao.deleteEvents(*deletes.toTypedArray())
                database.tennisDao.insertEvents(*container.asDatabaseModel())
                database.tennisDao.updateEvents(*currents.toTypedArray())
            }
        } else {
            throw RefreshException(RefreshExceptionType.CODE)
        }
    }

    override suspend fun updateEventsFunction() {
        val response = retrofitService.getTennisFilterResults(createNetworkRequest())
        if (response.code() == 200) {
            response.body()?.let { container ->
                dao.resetEventListStatus()
                dao.clearEntries()
                container.events.forEach { event ->
                    dao.changeEventListStatus(true, event.id)
                    event.entries?.forEach { athleteId ->
                        (dao as TennisDao).insertEntries(DatabaseTennisEntry(event.id, athleteId))
                    }
                }
            }
        } else {
            throw RefreshException(RefreshExceptionType.CODE)
        }
    }
}
