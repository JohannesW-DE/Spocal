package dev.weinsheimer.sportscalendar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import dev.weinsheimer.sportscalendar.R
import dev.weinsheimer.sportscalendar.databinding.FragmentAboutBinding
import dev.weinsheimer.sportscalendar.util.Sport
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import org.koin.android.ext.android.bind

class AboutFragment: Fragment() {
    private lateinit var binding: FragmentAboutBinding
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false)

        // get viewModel
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        binding.badminton.sport = "Badminton"
        binding.badminton.athletesfilter = "-"
        binding.badminton.athletes = "-"
        binding.badminton.eventsfilter = "-"
        binding.badminton.events = "-"
        binding.badminton.eventcategoriesfilter = "-"
        binding.badminton.eventcategories = "-"
        binding.badminton.result = "0"

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

        binding.tennis.sport = "Tennis"
        binding.tennis.athletesfilter = "-"
        binding.tennis.athletes = "-"
        binding.tennis.eventsfilter = "-"
        binding.tennis.events = "-"
        binding.tennis.eventcategoriesfilter = "-"
        binding.tennis.eventcategories = "-"
        binding.tennis.result = "0"

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

        binding.cycling.sport = "Rad"
        binding.cycling.athletesfilter = "-"
        binding.cycling.athletes = "-"
        binding.cycling.eventsfilter = "-"
        binding.cycling.events = "-"
        binding.cycling.eventcategoriesfilter = "-"
        binding.cycling.eventcategories = "-"
        binding.cycling.result = "0"

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