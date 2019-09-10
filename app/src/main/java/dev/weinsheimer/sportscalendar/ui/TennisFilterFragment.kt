package dev.weinsheimer.sportscalendar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TennisFilterFragment : FilterFragment() {
    private val viewModel by sharedViewModel<SharedViewModel>()

    override var sport = "tennis"
    override var selectAthletes = true
    override var selectEvents = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        athletes = viewModel.tennisAthletes
        events = viewModel.tennisEvents
        mainEventCategories = viewModel.tennisEventCategories
        eventCategories = viewModel.tennisEventCategories

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}

