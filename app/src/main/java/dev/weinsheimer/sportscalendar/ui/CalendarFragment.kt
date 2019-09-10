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

class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false)

        // get viewModel
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

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
        viewModel.cyclingEventCategories.observe(viewLifecycleOwner, Observer { })
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
            }, context!!)

        binding.calendarRecyclerView.adapter = adapter

        viewModel.calendarItems.observe(viewLifecycleOwner, Observer { items ->
            adapter.add(items)
            adapter.notifyDataSetChanged()
        })

        // refresh events
        binding.floatingActionButton.setOnClickListener {
            viewModel.updateCalendar()
        }

        //binding.floatingActionButton.hide()
        viewModel.calendarItems.observe(viewLifecycleOwner, Observer { items ->
            println("calendarItems -> " + items.size)
            if (items.isEmpty()) {
                binding.infoTextView.text = getString(R.string.calendar_no_filters)
                binding.infoTextView.visibility = View.VISIBLE
                //binding.floatingActionButton.hide()
            } else {
                binding.infoTextView.visibility = View.GONE
                //binding.floatingActionButton.show()
            }
        })
        return binding.root
    }

}
