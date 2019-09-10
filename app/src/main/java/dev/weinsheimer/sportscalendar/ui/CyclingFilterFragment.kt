package dev.weinsheimer.sportscalendar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import dev.weinsheimer.sportscalendar.domain.Athlete
import dev.weinsheimer.sportscalendar.domain.Event
import dev.weinsheimer.sportscalendar.domain.EventCategory
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CyclingFilterFragment: FilterFragment() {
    private val viewModel by sharedViewModel<SharedViewModel>()

    override var sport = "cycling"
    override var selectAthletes = false
    override var selectEvents = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        athletes = MutableLiveData<List<Athlete>>().apply { value = emptyList() }
        events = viewModel.cyclingEvents
        mainEventCategories = viewModel.cyclingEventMainCategories
        eventCategories = viewModel.cyclingEventCategories

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}