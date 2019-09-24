package dev.weinsheimer.sportscalendar.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import dev.weinsheimer.sportscalendar.domain.*
import dev.weinsheimer.sportscalendar.R
import kotlinx.coroutines.launch
import androidx.lifecycle.MediatorLiveData
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dev.weinsheimer.sportscalendar.network.RefreshWorker
import dev.weinsheimer.sportscalendar.repository.*
import dev.weinsheimer.sportscalendar.util.*
import kotlinx.coroutines.delay
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

const val REFRESH_DELAY = 10000L

/**
 * SharedViewModel acts as a store for (at least) api related data. AndroidViewModel is subclassed because the
 * application context is needed for managing the room database.
 */
@Suppress("IMPLICIT_CAST_TO_ANY")
class SharedViewModel: ViewModel(), KoinComponent {
    private val applicationContext: Context by inject()

    private val countryRepository: CountryRepository by inject()
    private val badmintonRepository: BadmintonRepository by inject()
    private val tennisRepository: TennisRepository by inject()
    private val cyclingRepository: CyclingRepository by inject()

    val isCalendarUpdated = MutableLiveData<Boolean>()

    var refreshWorkInfo: LiveData<WorkInfo>? = null
    val isDatabasePopulated = MutableLiveData<Boolean>().apply { value = false }



    // COMMON
    val countries = countryRepository.countries

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
        }
    }

    fun updateCalendar() {
        viewModelScope.launch {
            try {
                println("badmintonRepository.updateEvents()")
                badmintonRepository.updateEvents()
                println("tennisRepository.updateEvents()")
                tennisRepository.updateEvents()
                println("cyclingRepository.updateEvents()")
                cyclingRepository.updateEvents()

                isCalendarUpdated.value = true
                toast.value = R.string.calendar_update_success
            } catch (e: RefreshException) {
                isCalendarUpdated.value = false
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
                list.forEach {
                    println("@addSource -> ${it.dateFrom} / ${it.dateTo}")
                }
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
                println("populated YAY")
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