package dev.weinsheimer.sportscalendar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class BadmintonFilterFragment : FilterFragment() {
    private val viewModel by sharedViewModel<SharedViewModel>()

    override var sport = "badminton"
    override var selectAthletes = true
    override var selectEvents = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        athletes = viewModel.badmintonAthletes
        events = viewModel.badmintonEvents
        mainEventCategories = viewModel.badmintonEventMainCategories
        eventCategories = viewModel.badmintonEventCategories

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}


