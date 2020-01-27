package dev.weinsheimer.sportscalendar.repository

import androidx.lifecycle.Transformations
import dev.weinsheimer.sportscalendar.database.SpocalDB
import dev.weinsheimer.sportscalendar.database.dao.BadmintonDao
import dev.weinsheimer.sportscalendar.database.dao.BaseDao
import dev.weinsheimer.sportscalendar.database.model.DatabaseBadmintonEntry
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
class BadmintonRepository @Inject constructor(
    private val database: SpocalDB,
    private val retrofitService: ApiService
): BaseRepository() {
    override var dao: BaseDao = database.badmintonDao
    override var sport = Sport.BADMINTON

    override var athletes =
        Transformations.map(database.badmintonDao.getAthletes()) {
            it.asDomainModel()
        }

    override var events =
        Transformations.map(database.badmintonDao.getEvents()) {
            it.asDomainModel()
        }

    override var eventCategories =
        Transformations.map(database.badmintonDao.getEventCategories()) { categories ->
            categories.asDomainModel().filter { (5..10).contains(it.id) }
        }

    override var mainEventCategories =
        Transformations.map(database.badmintonDao.getMainEventCategories()) {
            it.asDomainModel()
        }

    override var calendarItems =
        Transformations.map(database.badmintonDao.getFilteredEvents()) {
            it.asCalendarListItems()
        }

    override suspend fun refreshAthletesFunction() {
        println("BADMINTON ATHLETE REFRESH 1")
        val response = retrofitService.getBadmintonAthletes()
        println("BADMINTON ATHLETE REFRESH 2")
        println(response.code())
        if (response.code() == 200) {
            response.body()?.let { container ->
                // objects in the database but not in the container
                val currents = database.badmintonDao.getCurrentAthletes()
                val futures = container.asDatabaseModel()
                val deletes = currents.filter { current ->
                    !futures.map { it.id }.contains(current.id)
                }
                currents.forEach { current ->
                    futures.find { it.id == current.id }?.let { future ->
                        current.update(future)
                    }
                }
                database.badmintonDao.deleteAthletes(*deletes.toTypedArray())
                database.badmintonDao.insertAthletes(*container.asDatabaseModel())
                database.badmintonDao.updateAthletes(*currents.toTypedArray())
            }
        } else {
            throw RefreshException(RefreshExceptionType.CODE)
        }
    }

    override suspend fun refresh() {
        refreshAthletes()
        refreshEventCategories()
        refreshEvents()
    }

    override suspend fun refreshEventCategoriesFunction() {
        val response = retrofitService.getBadmintonEventCategories()
        if (response.code() == 200) {
            response.body()?.let { container ->
                // objects in the database but not in the container
                val currents = database.badmintonDao.getCurrentEventCategories()
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
                database.badmintonDao.deleteEventCategories(*deletes.toTypedArray())
                database.badmintonDao.insertEventCategories(*container.asDatabaseModel())
                database.badmintonDao.updateEventCategories(*currents.toTypedArray())
            }
        } else {
            throw RefreshException(RefreshExceptionType.CODE)
        }
    }

    override suspend fun refreshEventsFunction() {
        val response = retrofitService.getBadmintonEvents()
        if (response.code() == 200) {
            response.body()?.let { container ->
                // objects in the database but not in the container
                val currents = database.badmintonDao.getCurrentEvents()
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
                database.badmintonDao.deleteEvents(*deletes.toTypedArray())
                //database.badmintonDao.insertEvents(*container.asDatabaseModel())
                container.asDatabaseModel().forEach {
                    println(it.name)
                    database.badmintonDao.insertEvents(it)
                }
                database.badmintonDao.updateEvents(*currents.toTypedArray())
            }
        } else {
            throw RefreshException(RefreshExceptionType.CODE)
        }
    }

    override suspend fun updateEventsFunction() {
        println("updatevents")
        val response = retrofitService.getBadmintonFilterResults(createNetworkRequest())
        println("update")
        if (response.code() == 200) {
            println(200)
            println(response.message())
            response.body()?.let { container ->
                println("container")
                dao.resetEventListStatus()
                dao.clearEntries()
                container.events.forEach { event ->
                    println("event")
                    dao.changeEventListStatus(true, event.id)
                    event.entries?.forEach { athleteId ->
                        println("DO ENTRY")
                        println(event.id)
                        println(athleteId)
                        (dao as BadmintonDao).insertEntries(
                            DatabaseBadmintonEntry(
                                event.id,
                                athleteId
                            )
                        )
                    }
                }
            }
        } else {
            throw RefreshException(RefreshExceptionType.CODE)
        }
    }
}
