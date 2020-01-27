package dev.weinsheimer.sportscalendar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import dev.weinsheimer.sportscalendar.R
import dev.weinsheimer.sportscalendar.util.Sport
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import javax.inject.Inject

class BadmintonFilterFragment : FilterFragment() {
    override var sport = Sport.BADMINTON

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        athletes = viewModel.badmintonAthletes
        events = viewModel.badmintonEvents
        mainEventCategories = viewModel.badmintonEventMainCategories
        eventCategories = viewModel.badmintonEventCategories

        eventName = context?.getString(R.string.fragment_about_badminton_event_name) ?: ""

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}


