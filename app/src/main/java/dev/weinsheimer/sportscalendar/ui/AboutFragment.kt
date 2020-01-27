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
import androidx.navigation.findNavController
import dagger.android.support.DaggerFragment
import dev.weinsheimer.sportscalendar.R
import dev.weinsheimer.sportscalendar.databinding.FragmentAboutBinding
import dev.weinsheimer.sportscalendar.util.Sport
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import javax.inject.Inject

/**
 * The about/statistics view. Shows a little about text and a few stats of every supported sport.
 */
class AboutFragment: DaggerFragment() {
    private lateinit var binding: FragmentAboutBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<SharedViewModel> { viewModelFactory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false)

        /** Badminton */
        binding.badminton.apply {
            sport = "Badminton"
            eventName =
                activity?.baseContext?.getString(R.string.fragment_about_badminton_event_name)
        }

        viewModel.badmintonAthletes.observe(viewLifecycleOwner, Observer { athletes ->
            binding.badminton.athletes = athletes.size.toString()
            binding.badminton.athletesfilter = athletes.filter { it.filter }.size.toString()
        })
        viewModel.badmintonEvents.observe(viewLifecycleOwner, Observer { events ->
            binding.badminton.events = events.size.toString()
            binding.badminton.eventsfilter = events.filter { it.filter }.size.toString()
        })

        viewModel.badmintonEventCategories.observe(viewLifecycleOwner, Observer { eventCategories ->
            binding.badminton.eventcategories = eventCategories.size.toString()
            binding.badminton.eventcategoriesfilter = eventCategories.filter { it.filter }.size.toString()
        })

        viewModel.calendarItems.observe(viewLifecycleOwner, Observer { calendarItems ->
            binding.badminton.result = calendarItems.filter { it.sport == Sport.BADMINTON }.size.toString()
        })

        binding.badminton.sportTextView.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_aboutFragment_to_badmintonFilterFragment)
        }

        /** Tennis */
        binding.tennis.apply {
            sport = "Tennis"
            eventName =
                activity?.baseContext?.getString(R.string.fragment_about_tennis_event_name)
        }

        viewModel.tennisAthletes.observe(viewLifecycleOwner, Observer { athletes ->
            binding.tennis.athletes = athletes.size.toString()
            binding.tennis.athletesfilter = athletes.filter { it.filter }.size.toString()
        })

        viewModel.tennisEvents.observe(viewLifecycleOwner, Observer { events ->
            binding.tennis.events = events.size.toString()
            binding.tennis.eventsfilter = events.filter { it.filter }.size.toString()
        })

        viewModel.tennisEventCategories.observe(viewLifecycleOwner, Observer { eventCategories ->
            binding.tennis.eventcategories = eventCategories.size.toString()
            binding.tennis.eventcategoriesfilter = eventCategories.filter { it.filter }.size.toString()
        })

        viewModel.calendarItems.observe(viewLifecycleOwner, Observer { calendarItems ->
            binding.tennis.result = calendarItems.filter { it.sport == Sport.TENNIS }.size.toString()
        })

        binding.tennis.sportTextView.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_aboutFragment_to_tennisFilterFragment)
        }

        /** Cycling */
        binding.cycling.apply {
            sport = "Radsport"
            eventName =
                activity?.baseContext?.getString(R.string.fragment_about_cycling_event_name)
            // no athletes
            athleteTextView.visibility = View.GONE
        }

        viewModel.cyclingEvents.observe(viewLifecycleOwner, Observer { events ->
            binding.cycling.events = events.size.toString()
            binding.cycling.eventsfilter = events.filter { it.filter }.size.toString()
        })

        viewModel.cyclingEventCategories.observe(viewLifecycleOwner, Observer { eventCategories ->
            binding.cycling.eventcategories = eventCategories.size.toString()
            binding.cycling.eventcategoriesfilter = eventCategories.filter { it.filter }.size.toString()
        })

        viewModel.calendarItems.observe(viewLifecycleOwner, Observer { calendarItems ->
            binding.cycling.result = calendarItems.filter { it.sport == Sport.CYCLING }.size.toString()
        })

        binding.cycling.sportTextView.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_aboutFragment_to_cyclingFilterFragment)
        }

        return binding.root
    }
}