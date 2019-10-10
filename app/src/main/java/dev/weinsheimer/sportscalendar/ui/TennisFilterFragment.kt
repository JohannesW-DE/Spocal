package dev.weinsheimer.sportscalendar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.weinsheimer.sportscalendar.R
import dev.weinsheimer.sportscalendar.util.Sport
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TennisFilterFragment : FilterFragment() {
    private val viewModel by sharedViewModel<SharedViewModel>()

    override var sport = Sport.TENNIS
    override var selectAthletes = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        athletes = viewModel.tennisAthletes
        events = viewModel.tennisEvents
        mainEventCategories = viewModel.tennisEventCategories
        eventCategories = viewModel.tennisEventCategories

        eventName = context?.getString(R.string.fragment_about_tennis_event_name) ?: ""

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}

