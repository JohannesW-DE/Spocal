package dev.weinsheimer.sportscalendar.viewmodels

import android.app.Application
import androidx.lifecycle.*
import dev.weinsheimer.sportscalendar.database.getDatabase
import dev.weinsheimer.sportscalendar.domain.*
import dev.weinsheimer.sportscalendar.R
import kotlinx.coroutines.launch
import androidx.lifecycle.MediatorLiveData
import dev.weinsheimer.sportscalendar.database.SpocalDB
import dev.weinsheimer.sportscalendar.network.Api
import dev.weinsheimer.sportscalendar.util.RefreshException
import dev.weinsheimer.sportscalendar.util.RefreshExceptionType
import dev.weinsheimer.sportscalendar.repository.*
import dev.weinsheimer.sportscalendar.util.Sport
import kotlinx.coroutines.delay

const val REFRESH_DELAY = 10000L

/**
 * SharedViewModel acts as a store for (at least) api related data. AndroidViewModel is subclassed because the
 * application context is needed for managing the room database.
 */
@Suppress("IMPLICIT_CAST_TO_ANY")
class SharedViewModel(
    val commonRepository: CommonRepository,
    val badmintonRepository: BadmintonRepository,
    val tennisRepository: TennisRepository,
    val cyclingRepository: CyclingRepository
) : ViewModel() {
    val refreshCompleted = MutableLiveData<Boolean>()
    val calendarUpdateCompleted = MutableLiveData<Boolean>()

    // COMMON
    val countries = commonRepository.countries

    // TENNIS
    val tennisAthletes = tennisRepository.athletes
    val tennisEventCategories = tennisRepository.eventCategories
    val tennisEvents = tennisRepository.events

    // BADMINTON
    val badmintonAthletes = badmintonRepository.athletes
    val badmintonEventCategories = badmintonRepository.eventCategories
    val badmintonEvents = badmintonRepository.events
    val badmintonEventMainCategories = badmintonRepository.mainEventCategories

    // CYCLING
    val cyclingEventCategories = cyclingRepository.eventCategories
    val cyclingEvents = cyclingRepository.events
    val cyclingEventMainCategories =
        Transformations.map(cyclingEventCategories) { categories ->
            categories.filter { it.mainCategory == null }
        }

    // MISC
    private val _toast = MutableLiveData<Int>()
    val toast
        get() = _toast

    var calendarItems = MediatorLiveData<List<CalendarListItem>>()

    /**
     * save filters
     */
    fun updateFilters(sport: String, athletes: List<Athlete>?, eventCategories: List<EventCategory>?, events: List<Event>?) {
        viewModelScope.launch {
            when(sport) {
                "badminton" -> badmintonRepository.updateFilter(athletes, eventCategories, events)
                "tennis" -> tennisRepository.updateFilter(athletes, eventCategories, events)
                "cycling" -> cyclingRepository.updateFilter(athletes, eventCategories, events)
            }
            println("UPDATING CALENDAR?????")
            //delay(1000)
            //updateCalendar()
        }
    }

    fun updateCalendar() {
        viewModelScope.launch {
            try {
                badmintonRepository.updateEvents()
                tennisRepository.updateEvents()
                cyclingRepository.updateEvents()

                calendarUpdateCompleted.value = true
                toast.value = R.string.calendar_update_success
            } catch (e: RefreshException) {
                calendarUpdateCompleted.value = false
                toast.value = R.string.calendar_update_fail
            }
        }
    }

    fun hideCalendarItem(sport: String, eventId: Int) {
        viewModelScope.launch {
            when(sport) {
                "badminton" -> badmintonRepository.hideEvent(eventId)
                "tennis" -> tennisRepository.hideEvent(eventId)
                "cycling" -> cyclingRepository.hideEvent(eventId)
            }
        }
    }

    init {
        badmintonRepository.setup()
        tennisRepository.setup()
        cyclingRepository.setup()

        /**
         * mediate calendar items
         */
        calendarItems.addSource(badmintonRepository.calendarItemsWithEntries) { calendarItems ->
            val result = mutableListOf<CalendarListItem>()
            this.calendarItems.value?.let { list ->
                result.addAll(list.filter { it.sport != Sport.BADMINTON })
            }
            result.addAll(calendarItems)
            this.calendarItems.value = result
        }

        calendarItems.addSource(cyclingRepository.calendarItems) { calendarItems ->
            val result = mutableListOf<CalendarListItem>()
            this.calendarItems.value?.let { list ->
                result.addAll(list.filter { it.sport != Sport.CYCLING })
            }
            result.addAll(calendarItems)
            this.calendarItems.value = result
        }

        calendarItems.addSource(tennisRepository.calendarItemsWithEntries) { calendarItems ->
            val result = mutableListOf<CalendarListItem>()
            this.calendarItems.value?.let { list ->
                list.forEach {
                    println("@addSource -> ${it.dateFrom} / ${it.dateTo}")
                }
                result.addAll(list.filter { it.sport != Sport.TENNIS })
            }
            result.addAll(calendarItems)
            this.calendarItems.value = result
        }

        refresh()
    }

    private fun refresh() {
        viewModelScope.launch {
            try {
                // common
                commonRepository.refreshCountries()
                // badminton
                badmintonRepository.refreshAthletes()
                badmintonRepository.refreshEventCategories()
                badmintonRepository.refreshEvents()
                // tennis
                tennisRepository.refreshAthletes()
                tennisRepository.refreshEventCategories()
                tennisRepository.refreshEvents()
                // cycling
                //cyclingRepository.refreshAthletes()
                cyclingRepository.refreshEventCategories()
                cyclingRepository.refreshEvents()

                refreshCompleted.value = true

                // update calendar as well if it failed the last time
                calendarUpdateCompleted.value?.let {
                    if (!it) {
                        updateCalendar()
                    }
                }

            } catch (e: RefreshException) {
                // only send the toast once, and let the actionbar indicate the existing problem otherwise
                refreshCompleted.value.let {
                    if (it == null) {
                        println("SEND NEW MSG")
                        when(e.message) {
                            RefreshExceptionType.CONNECTION.toString() -> toast.value = R.string.error_network_connection
                            RefreshExceptionType.CODE.toString() -> toast.value = R.string.error_network_code
                            RefreshExceptionType.FORMAT.toString() -> toast.value = R.string.error_network_format
                        }
                    }
                }
                refreshCompleted.value = false
                delay(REFRESH_DELAY)
                refresh()
            }
        }
    }
}