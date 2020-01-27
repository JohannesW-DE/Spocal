package dev.weinsheimer.sportscalendar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import dev.weinsheimer.sportscalendar.R
import dev.weinsheimer.sportscalendar.domain.Athlete
import dev.weinsheimer.sportscalendar.util.Sport
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import javax.inject.Inject


class CyclingFilterFragment: FilterFragment() {
    override var sport = Sport.CYCLING
    override var selectAthletes = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        athletes = MutableLiveData<List<Athlete>>().apply { value = emptyList() }
        events = viewModel.cyclingEvents
        mainEventCategories = viewModel.cyclingEventMainCategories
        eventCategories = viewModel.cyclingEventCategories

        eventName = context?.getString(R.string.fragment_about_cycling_event_name) ?: ""

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}