package dev.weinsheimer.sportscalendar.repository

import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.room.Database
import com.google.gson.Gson
import dev.weinsheimer.sportscalendar.database.*
import dev.weinsheimer.sportscalendar.domain.*
import dev.weinsheimer.sportscalendar.network.*
import dev.weinsheimer.sportscalendar.util.RefreshException
import dev.weinsheimer.sportscalendar.util.RefreshExceptionType
import dev.weinsheimer.sportscalendar.util.refresh
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response


abstract class BaseRepository(private val database: SpocalDB) {
    abstract var sport: String
    abstract var dao: BaseDao

    abstract var athletes: LiveData<List<Athlete>>
    abstract var events: LiveData<List<Event>>
    abstract var mainEventCategories: LiveData<List<EventCategory>>
    abstract var eventCategories: LiveData<List<EventCategory>>
    abstract var calendarItems: LiveData<List<CalendarListItem>>

    var calendarItemsWithEntries = MediatorLiveData<List<CalendarListItem>>().apply { postValue(emptyList()) }

    fun setup() {
        calendarItemsWithEntries.addSource(calendarItems) { items ->
            println("setup -> calendarItems")
            athletes.value?.let { athletes ->
                items.forEach { item ->
                    item.entries?.let { entries ->
                        item.athletes = athletes.filter { entries.contains(it.id) }
                    }
                }
            }
            calendarItemsWithEntries.value = items
        }

        calendarItemsWithEntries.addSource(athletes) { athletes ->
            println("setup -> athletes")
            calendarItems.value?.let { items ->
                items.forEach { item ->
                    item.entries?.let { entries ->
                        item.athletes = athletes.filter { entries.contains(it.id) }
                    }
                }
            }
            calendarItemsWithEntries.value = calendarItemsWithEntries.value
        }
    }

    open suspend fun updateFilter(athletes: List<Athlete>?, eventCategories: List<EventCategory>?, events: List<Event>?) {
        withContext(Dispatchers.IO) {
            dao.resetAthleteFilters()
            dao.resetEventCategoryFilters()
            dao.resetEventFilters()

            athletes?.let {
                it.forEach { athlete ->
                    dao.updateAthleteFilter(athlete.id)
                }
            }
            eventCategories?.let {
                it.forEach { eventCategory ->
                    dao.updateEventCategoryFilter(eventCategory.id)
                }
            }
            events?.let {
                it.forEach { event ->
                    dao.updateEventFilter(event.id)
                }
            }
            println("UPDATED????")
        }
    }

    suspend fun hideEvent(eventId: Int) {
        withContext(Dispatchers.IO) {
            dao.changeEventListStatus(false, eventId)
        }
    }

    /**
     * API + DAO calls
     */
    open suspend fun refreshAthletesFunction() {}
    suspend fun refreshAthletes() {
        refresh {
            withContext(Dispatchers.IO) {
                refreshAthletesFunction()
            }
        }
    }

    open suspend fun refreshEventsFunction() {}
    suspend fun refreshEvents() {
        refresh {
            withContext(Dispatchers.IO) {
                refreshEventsFunction()
            }
        }
    }
    open suspend fun refreshEventCategoriesFunction() {}
    suspend fun refreshEventCategories() {
        refresh {
            withContext(Dispatchers.IO) {
                refreshEventCategoriesFunction()
            }
        }
    }
    open suspend fun updateEventsFunction() {}
    suspend fun updateEvents() {
        refresh {
            withContext(Dispatchers.IO) {
                updateEventsFunction()
            }
        }
    }

    fun createNetworkRequest(): NetworkFilterResultsRequest {
        var athleteIds = emptyList<Int>()
        var eventCategoryIds = emptyList<Int>()
        var eventIds = emptyList<Int>()
        println("createNetworkRequest")
        println(athletes.value?.filter { it.filter }?.size)
        println(eventCategories.value?.filter { it.filter }?.size)
        println(events.value?.filter { it.filter }?.size)
        athletes.value?.filter { it.filter }?.let { athletes ->
            athleteIds = athletes.map { it.id }
        }
        eventCategories.value?.filter { it.filter }?.let { eventCategories ->
            eventCategoryIds = eventCategories.map { it.id }
        }
        events.value?.filter { it.filter }?.let { events ->
            eventIds = events.map { it.id }
        }
        println(athleteIds)
        return NetworkFilterResultsRequest(athleteIds, eventIds, eventCategoryIds)
    }

}
