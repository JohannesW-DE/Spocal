package dev.weinsheimer.sportscalendar.viewmodels

import android.content.Context
import androidx.lifecycle.*
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dev.weinsheimer.sportscalendar.R
import dev.weinsheimer.sportscalendar.domain.Athlete
import dev.weinsheimer.sportscalendar.domain.CalendarListItem
import dev.weinsheimer.sportscalendar.domain.Event
import dev.weinsheimer.sportscalendar.domain.EventCategory
import dev.weinsheimer.sportscalendar.network.RefreshWorker
import dev.weinsheimer.sportscalendar.repository.BadmintonRepository
import dev.weinsheimer.sportscalendar.repository.CountryRepository
import dev.weinsheimer.sportscalendar.repository.CyclingRepository
import dev.weinsheimer.sportscalendar.repository.TennisRepository
import dev.weinsheimer.sportscalendar.util.RefreshException
import dev.weinsheimer.sportscalendar.util.Sport
import dev.weinsheimer.sportscalendar.util.asDate
import dev.weinsheimer.sportscalendar.util.asString
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * The source of information for every UI element.
 */
class SharedViewModel @Inject constructor(
    private val applicationContext: Context,
    private val countryRepository: CountryRepository,
    private val badmintonRepository: BadmintonRepository,
    private val tennisRepository: TennisRepository,
    private val cyclingRepository: CyclingRepository
): ViewModel() {
    var refreshWorkInfo: LiveData<WorkInfo>? = null
    val isDatabasePopulated
            = MutableLiveData<Boolean>().apply { value = false }
    val isCalendarUpdatedWithCurrentFilterSelection
            = MutableLiveData<Boolean>().apply { value = false }

    override fun onCleared() {
        super.onCleared()
        println("CLEARED VIEWMODEL")
    }

    init {
        println("VIEWMODEL CREATED")
    }


    /**
     * Sports (related) LiveData
     */
    // Country
    val countries = countryRepository.countries

    // Tennis
    val tennisAthletes = tennisRepository.athletes
    val tennisEventCategories = tennisRepository.eventCategories
    val tennisEvents = tennisRepository.events

    // Badminton
    val badmintonAthletes = badmintonRepository.athletes
    val badmintonEventCategories = badmintonRepository.eventCategories
    val badmintonEvents = badmintonRepository.events
    val badmintonEventMainCategories = badmintonRepository.mainEventCategories

    // Cycling
    val cyclingEventCategories = cyclingRepository.eventCategories
    val cyclingEvents = cyclingRepository.events
    val cyclingEventMainCategories =
        Transformations.map(cyclingEventCategories) { categories ->
            categories.filter { it.mainCategory == null }
        }

    var filterChangeMediator = MediatorLiveData<Any>()
    var filterCount = MutableLiveData<Int>().apply { value = 0 }

    // MISC
    private val _toast = MutableLiveData<Int>()
    val toast
        get() = _toast

    var calendarItems = MediatorLiveData<List<CalendarListItem>>()

    /**
     * save filters
     */
    fun updateFilters(sport: Sport, athletes: List<Athlete>?, eventCategories: List<EventCategory>?, events: List<Event>?) {
        viewModelScope.launch {
            when(sport) {
                Sport.BADMINTON -> badmintonRepository.updateFilter(athletes, eventCategories, events)
                Sport.TENNIS -> tennisRepository.updateFilter(athletes, eventCategories, events)
                Sport.CYCLING -> cyclingRepository.updateFilter(athletes, eventCategories, events)
            }
        }
        isCalendarUpdatedWithCurrentFilterSelection.value = false
    }

    fun updateCalendar() {
        println("updateCalendar()")
        viewModelScope.launch {
            try {
                println("updatecalendar")
                badmintonRepository.updateEvents()
                println(1)
                tennisRepository.updateEvents()
                println(2)
                cyclingRepository.updateEvents()
                println(3)

                isCalendarUpdatedWithCurrentFilterSelection.value = true

                toast.value = R.string.calendar_update_success
            } catch (e: RefreshException) {
                toast.value = R.string.calendar_update_fail
            }
        }
    }

    fun hideCalendarItem(sport: Sport, eventId: Int) {
        viewModelScope.launch {
            when(sport) {
                Sport.BADMINTON -> badmintonRepository.hideEvent(eventId)
                Sport.TENNIS -> tennisRepository.hideEvent(eventId)
                Sport.CYCLING -> cyclingRepository.hideEvent(eventId)
            }
        }
    }

    private fun updateFilterCount() {
        println("FILTERCOUNTUPDATE")
        var filterCount = 0
        filterCount += badmintonRepository.filterCount()
        filterCount += tennisRepository.filterCount()
        filterCount += cyclingRepository.filterCount()
        this.filterCount.value = filterCount
    }

    init {
        filterChangeMediator.addSource(badmintonAthletes) { updateFilterCount() }
        filterChangeMediator.addSource(badmintonEventCategories) { updateFilterCount() }
        filterChangeMediator.addSource(badmintonEvents) { updateFilterCount() }
        filterChangeMediator.addSource(tennisAthletes) { updateFilterCount() }
        filterChangeMediator.addSource(tennisEventCategories) { updateFilterCount() }
        filterChangeMediator.addSource(tennisEvents) { updateFilterCount() }
        filterChangeMediator.addSource(cyclingEventCategories) { updateFilterCount() }
        filterChangeMediator.addSource(cyclingEvents) { updateFilterCount() }
        /**
         * mediate calendar items
         */
        calendarItems.addSource(badmintonRepository.calendarItems) { calendarItems ->
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

        calendarItems.addSource(tennisRepository.calendarItems) { calendarItems ->
            val result = mutableListOf<CalendarListItem>()
            this.calendarItems.value?.let { list ->
                result.addAll(list.filter { it.sport != Sport.TENNIS })
            }
            result.addAll(calendarItems)
            this.calendarItems.value = result
        }

        // check if there was an database refresh since the app was started the last time
        val lastStart = applicationContext
            .getSharedPreferences("spocal", Context.MODE_PRIVATE)
            .getString("lastStart", null)

        val refreshDate = applicationContext
            .getSharedPreferences("spocal", Context.MODE_PRIVATE)
            .getString("refreshDate", null)

        if (lastStart != null && refreshDate != null) {
            if (lastStart.asDate() < refreshDate.asDate()) {
                toast.value = R.string.toast_refresh_complete
            }
        }

        // run RefreshWorker for the one time and the one time only if the db isn't initialized yet
        println("checking refreshDate")
        refreshDate.let {
            if (it == null) {
                refreshWorkInfo = runRefreshWorker()
            } else {
                isDatabasePopulated.value = true
            }
        }

        // remember the app start
        with (applicationContext.getSharedPreferences("spocal", Context.MODE_PRIVATE).edit()) {
            putString("lastStart", java.util.Calendar.getInstance().time.asString())
            commit()
        }
    }

    private fun runRefreshWorker() : LiveData<WorkInfo> {
        val request = OneTimeWorkRequestBuilder<RefreshWorker>()
            .addTag(RefreshWorker.WORK_NAME_UNIQUE)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 15L, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(request)
        return WorkManager.getInstance(applicationContext).getWorkInfoByIdLiveData(request.id)
    }

    fun updateRefreshDate() {
        with (applicationContext.getSharedPreferences("spocal", Context.MODE_PRIVATE).edit()) {
            putString("refreshDate", Calendar.getInstance().time.asString())
            commit()
        }
        isDatabasePopulated.value = true
    }
}