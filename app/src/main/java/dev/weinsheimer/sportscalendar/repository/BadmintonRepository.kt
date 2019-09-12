package dev.weinsheimer.sportscalendar.repository

import androidx.lifecycle.Transformations
import dev.weinsheimer.sportscalendar.database.*
import dev.weinsheimer.sportscalendar.database.dao.BadmintonDao
import dev.weinsheimer.sportscalendar.database.model.DatabaseBadmintonEntry
import dev.weinsheimer.sportscalendar.database.model.asCalendarListItems
import dev.weinsheimer.sportscalendar.database.model.asDomainModel
import dev.weinsheimer.sportscalendar.database.model.update
import dev.weinsheimer.sportscalendar.network.*
import dev.weinsheimer.sportscalendar.util.RefreshException
import dev.weinsheimer.sportscalendar.util.RefreshExceptionType

class BadmintonRepository(val database: SpocalDB, val retrofitService: ApiService): BaseRepository(database) {
    override var sport = "badminton"
    override var dao: BaseDao = database.badmintonDao

    override var athletes =
        Transformations.map(database.badmintonDao.getAthletesWithCountry()) {
            println(sport + " - athletes -> " + it.size)
            it.asDomainModel()
        }

    override var events =
        Transformations.map(database.badmintonDao.getEvents()) {
            println(sport + " - events -> " + it.size)
            it.asDomainModel()
        }

    override var eventCategories =
        Transformations.map(database.badmintonDao.getEventCategories()) { categories ->
            println(sport + " - eventCategories -> " + categories.size)
            println(categories.filter { entry -> entry.filter }.size)
            categories.asDomainModel().filter { (5..10).contains(it.id) }
        }

    override var mainEventCategories =
        Transformations.map(database.badmintonDao.getMainEventCategories()) {
            println(sport + " - mainEventCategories " + it.size)
            it.asDomainModel()
        }

    override var calendarItems =
        Transformations.map(database.badmintonDao.getFilteredEvents()) {
            println(sport + " - _calendarItems_ -> " + it.size)
            it.asCalendarListItems()
        }

    override suspend fun refreshAthletesFunction() {
        val response = retrofitService.getBadmintonAthletes()
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
                database.badmintonDao.insertEvents(*container.asDatabaseModel())
                database.badmintonDao.updateEvents(*currents.toTypedArray())
            }
        } else {
            throw RefreshException(RefreshExceptionType.CODE)
        }
    }

    override suspend fun updateEventsFunction() {
        val response = retrofitService.getBadmintonFilterResults(createNetworkRequest())
        if (response.code() == 200) {
            response.body()?.let { container ->
                dao.resetEventListStatus()
                dao.clearEntries()
                container.events.forEach { event ->
                    dao.changeEventListStatus(true, event.id)
                    event.entries?.forEach { athleteId ->
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
