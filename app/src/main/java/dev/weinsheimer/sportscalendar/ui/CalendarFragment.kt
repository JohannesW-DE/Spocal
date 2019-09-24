package dev.weinsheimer.sportscalendar.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import dev.weinsheimer.sportscalendar.*
import dev.weinsheimer.sportscalendar.databinding.FragmentCalendarBinding
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding

    private val viewModel by sharedViewModel<SharedViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false)

        /**
         * FIGURE OUT A BETTER WAY TO FORCE THE INITIAL PROPAGATION
         */

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
            viewModel.updateCalendar()
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
        viewModel.isDatabasePopulated.value?.let { isDatabasePopulated ->
            binding.infoTextView.visibility = View.GONE
            if (!isDatabasePopulated) {
                binding.infoTextView.apply {
                    text = getString(R.string.calendar_no_data)
                    visibility = View.VISIBLE
                }
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
                viewModel.calendarItems.value?.let { itemList ->
                    if (itemList.isEmpty()) {
                        binding.infoTextView.apply {
                            text = getString(R.string.calendar_no_items)
                            visibility = View.VISIBLE
                        }
                    } else {
                        binding.infoTextView.visibility = View.GONE
                    }
                }
            }
        }
    }

}
