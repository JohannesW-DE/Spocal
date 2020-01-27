package dev.weinsheimer.sportscalendar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerFragment
import dev.weinsheimer.sportscalendar.R
import dev.weinsheimer.sportscalendar.databinding.FragmentCalendarBinding
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import javax.inject.Inject

class CalendarFragment : DaggerFragment() {
    private lateinit var binding: FragmentCalendarBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<SharedViewModel> { viewModelFactory }

    init {
        println("INIT CALENDAR")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false)

        // not ideal
        viewModel.countries.observe(viewLifecycleOwner, Observer { })
        viewModel.badmintonAthletes.observe(viewLifecycleOwner, Observer { })
        viewModel.badmintonEventCategories.observe(viewLifecycleOwner, Observer { })
        viewModel.badmintonEvents.observe(viewLifecycleOwner, Observer { })
        viewModel.tennisAthletes.observe(viewLifecycleOwner, Observer {  })
        viewModel.tennisEventCategories.observe(viewLifecycleOwner, Observer {  })
        viewModel.tennisEvents.observe(viewLifecycleOwner, Observer {  })
        viewModel.cyclingEventCategories.observe(viewLifecycleOwner, Observer {  })
        viewModel.cyclingEvents.observe(viewLifecycleOwner, Observer {  })
        viewModel.cyclingEventMainCategories.observe(viewLifecycleOwner, Observer {  })
        viewModel.filterChangeMediator.observe(viewLifecycleOwner, Observer {  })
        /**
         * adapter stuff
         */
        // redirect the adapters onClick events to the viewmodel
        val adapter =
            CalendarAdapter(CalendarListener { sport, id ->
                viewModel.hideCalendarItem(sport, id)
            }, binding.calendarRecyclerView.context)

        binding.calendarRecyclerView.adapter = adapter

        viewModel.calendarItems.observe(viewLifecycleOwner, Observer {
            adapter.add(it)
        })

        // refresh events
        binding.floatingActionButton.setOnClickListener {
            println("FAB")
            viewModel.updateCalendar()
            updateInfoText()
        }

        viewModel.isDatabasePopulated.observe(viewLifecycleOwner, Observer {
            updateInfoText()
        })

        viewModel.calendarItems.observe(viewLifecycleOwner, Observer {
            updateInfoText()
        })

        binding.progressBar.visibility = View.INVISIBLE

        return binding.root
    }

    private fun updateInfoText() {
        // hide everything by default
        binding.infoTextView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.floatingActionButton.show()

        // database is populated not yet
        viewModel.isDatabasePopulated.value?.let {
            if (!it) {
                binding.infoTextView.apply {
                    text = getString(R.string.calendar_no_data)
                    visibility = View.VISIBLE
                }
                binding.progressBar.visibility = View.VISIBLE
                binding.floatingActionButton.hide()
                return
            }
        }

        // no filter selected
        viewModel.filterCount.value?.let {
            println("FILTERCOUNT")
            println(it)
            if (it == 0) {
                binding.infoTextView.apply {
                    text = getString(R.string.calendar_no_filters)
                    visibility = View.VISIBLE
                }
                return
            }
        }

        // calendar is still empty
        viewModel.calendarItems.value?.let { items ->
            viewModel.isCalendarUpdatedWithCurrentFilterSelection.value?.let { updated ->
                if (items.isEmpty() && updated) {
                    binding.infoTextView.apply {
                        text = getString(R.string.calendar_no_results)
                        visibility = View.VISIBLE
                    }
                }
            }
        }
    }

}
