package dev.weinsheimer.sportscalendar.repository

import androidx.lifecycle.Transformations
import dev.weinsheimer.sportscalendar.database.SpocalDB
import dev.weinsheimer.sportscalendar.database.dao.BaseDao
import dev.weinsheimer.sportscalendar.database.model.asCalendarListItems
import dev.weinsheimer.sportscalendar.database.model.asDomainModel
import dev.weinsheimer.sportscalendar.database.model.update
import dev.weinsheimer.sportscalendar.network.ApiService
import dev.weinsheimer.sportscalendar.network.asDatabaseModel
import dev.weinsheimer.sportscalendar.util.RefreshException
import dev.weinsheimer.sportscalendar.util.RefreshExceptionType
import dev.weinsheimer.sportscalendar.util.Sport
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CyclingRepository @Inject constructor(
    private val database: SpocalDB,
    private val retrofitService: ApiService
): BaseRepository() {
    override var dao: BaseDao = database.cyclingDao
    override var sport = Sport.CYCLING

    /**
     * DAO calls
     */
    override var athletes =
        Transformations.map(database.cyclingDao.getAthletes()) {
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

    override suspend fun refresh() {
        refreshEventCategories()
        refreshEvents()
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
                println("container")
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