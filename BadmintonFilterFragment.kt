package dev.weinsheimer.sportscalendar.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.chip.Chip
import dev.weinsheimer.sportscalendar.R
import dev.weinsheimer.sportscalendar.databinding.FragmentBadmintonFilterBinding
import dev.weinsheimer.sportscalendar.databinding.FragmentBadmintonnFilterBinding
import dev.weinsheimer.sportscalendar.domain.Athlete
import dev.weinsheimer.sportscalendar.domain.Event
import dev.weinsheimer.sportscalendar.domain.EventCategory
import dev.weinsheimer.sportscalendar.util.*
import dev.weinsheimer.sportscalendar.viewmodels.FilterContainer
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import dev.weinsheimer.sportscalendar.viewmodels.add
import dev.weinsheimer.sportscalendar.viewmodels.remove
import timber.log.Timber

class BadmintonFilterFragment : Fragment() {
    private lateinit var binding: FragmentBadmintonnFilterBinding
    private lateinit var viewModel: SharedViewModel

    private val selectedAthlete = MutableLiveData<Athlete>()
    private val selectedEvent = MutableLiveData<Event>()
    private val selectedCategory = MutableLiveData<EventCategory>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_badmintonn_filter, container, false)

        binding.lifecycleOwner = this

        binding.selectedAthlete = selectedAthlete
        binding.selectedEvent = selectedEvent

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        /**
         * VIEW/VIEWMODEL -> ATHLETE
         */
        // populate autocompletetextview
        viewModel.badmintonAthletes.observe(viewLifecycleOwner, Observer { athletes ->
            binding.athleteAutoCompleteTextView.setAdapter(BaseAdapter(binding.athleteFilterChipGroup.context, athletes))
        })

        // react to selection
        binding.athleteAutoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                selectedAthlete.value = binding.athleteAutoCompleteTextView.adapter.getItem(position) as Athlete
            }

        // react to text change, disabling adding
        binding.athleteAutoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                selectedAthlete.value = null
            }
        })

        // react to add
        binding.athleteButton.setOnClickListener {
            selectedAthlete.value?.let { athlete ->
                viewModel.badmintonCurrentFilterContainer.add<Athlete>(athlete)
            }
            selectedAthlete.value = null

            // try some stuff to clear focus and hide keyboard
            binding.athleteAutoCompleteTextView.setText("")
            binding.athleteAutoCompleteTextView.clearComposingText()
            binding.athleteAutoCompleteTextView.clearFocus()

            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
        }

        /**
         * VIEW/VIEWMODEL -> EVENTS
         */
        // populate spinner
        viewModel.badmintonEventMainCategories.observe(viewLifecycleOwner, Observer { categories ->
            binding.eventCategorySpinner.adapter =
                ArrayAdapter(
                    binding.eventCategorySpinner.context,
                    R.layout.spinner_row,
                    listOf(EventCategory(0, getString(R.string.badminton_filter_categories_all), null)) + categories
                )
        })

        // react to spinner selection
        binding.eventCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Timber.i("nothing")
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory.value = binding.eventCategorySpinner.adapter.getItem(position) as EventCategory
                updateEventAutoComplete()
                // react to refreshed events
                binding.eventAutoCompleteTextView.text = binding.eventAutoCompleteTextView.text
                binding.eventAutoCompleteTextView.setSelection(binding.eventAutoCompleteTextView.text.length)
            }
        }

        // react to autocompletetextview selection
        binding.eventAutoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                selectedEvent.value = binding.eventAutoCompleteTextView.adapter.getItem(position) as Event
            }

        // react to text change, disabling adding
        binding.eventAutoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                selectedEvent.value = null
            }
        })

        // react to add
        binding.eventButton.setOnClickListener {
            selectedEvent.value?.let { event ->
                viewModel.badmintonCurrentFilterContainer.add<Event>(event)
            }
            selectedEvent.value = null

            // try some stuff to clear focus and hide keyboard
            binding.eventAutoCompleteTextView.setText("")
            binding.eventAutoCompleteTextView.clearComposingText()
            binding.eventAutoCompleteTextView.clearFocus()

            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
        }

        /**
         * VIEW/VIEWMODEL -> EVENT CATEGORIES
         */
        viewModel.badmintonEventCategories.value?.
            filter { it.name.contains(" Tour ") && it.mainCategory != null }?.
            forEach { category ->
                binding.categoryFilterChipGroup.addView(
                    badmintonCategoryMap[category.name]?.let { text ->
                        Chip(binding.categoryFilterChipGroup.context).createCheckableFilterChip(
                            text,
                            CompoundButton.OnCheckedChangeListener { _, isChecked ->
                                if (isChecked) {
                                    viewModel.badmintonCurrentFilterContainer.add<EventCategory>(category)
                                } else {
                                    viewModel.badmintonCurrentFilterContainer.remove<EventCategory>(category)
                                }
                            },
                            category
                        )
                    }
                )
            }

        /**
         * FAB
         */
        binding.updateFloatingActionButton.setOnClickListener {
            viewModel.updateFilters("badminton")
        }

        /**
         * Filter observation
         */
        viewModel.badmintonCurrentFilterContainer.observe(viewLifecycleOwner, Observer { filterContainer ->
            Timber.i("viewModel.badmintonFilter.observe")

            // ATHLETES
            filterContainer.athletes.forEach { athlete ->
                println(athlete)
                if (!binding.athleteFilterChipGroup.contains(athlete.id)) {
                    binding.athleteFilterChipGroup.addView(
                        Chip(binding.athleteFilterChipGroup.context).createFilterChip(
                            athlete.name,
                            View.OnClickListener {
                                println("removing athlete")
                                viewModel.badmintonCurrentFilterContainer.remove<Athlete>(athlete)
                            },
                            athlete
                        )
                    )
                }
            }
            binding.athleteFilterChipGroup.removeOtherChildren(filterContainer.athletes.map { it.id })
            if (binding.athleteFilterChipGroup.childCount > 0) {
                binding.athleteFilterChipGroup.visibility = View.VISIBLE
            } else {
                binding.athleteFilterChipGroup.visibility = View.GONE
            }

            // EVENTS
            filterContainer.events.forEach { event ->
                if (!binding.eventFilterChipGroup.contains(event.id)) {
                    binding.eventFilterChipGroup.addView(
                        Chip(binding.eventFilterChipGroup.context).createFilterChip(
                            event.name,
                            View.OnClickListener {
                                viewModel.badmintonCurrentFilterContainer.remove<Event>(event)
                            },
                            event
                        )
                    )
                }
            }
            binding.eventFilterChipGroup.removeOtherChildren(filterContainer.events.map { it.id })
            if (binding.eventFilterChipGroup.childCount > 0) {
                binding.eventFilterChipGroup.visibility = View.VISIBLE
            } else {
                binding.eventFilterChipGroup.visibility = View.GONE
            }

            // CATEGORIES
            val ids = filterContainer.eventCategories.map { it.id }
            for (index in 0 until binding.categoryFilterChipGroup.childCount) {
                val chip = binding.categoryFilterChipGroup.getChildAt(index) as Chip
                if (ids.contains(chip.id)) {
                    chip.isChecked = true
                }
            }
        })

        return binding.root // !!!
    }

    fun updateEventAutoComplete() {
        selectedCategory.value?.let { selectedCategory ->
            if (selectedCategory.id == 0) {
                binding.eventAutoCompleteTextView.threshold = 1

                viewModel.badmintonEvents.value?.let { events ->
                    events.sortedBy { it.name }.let { sortedEvents ->
                        binding.eventAutoCompleteTextView.setAdapter(
                            BaseAdapter(
                                binding.eventAutoCompleteTextView.context,
                                sortedEvents.toMutableList()
                            )
                        )
                    }
                }
            } else {
                binding.eventAutoCompleteTextView.threshold = 1

                viewModel.badmintonEventCategories.value?.let { categories ->
                    categories.filter { it.mainCategory == selectedCategory.id }.map { it.id }.let { subCategories ->
                        viewModel.badmintonEvents.value?.let { events ->
                            events.filter { subCategories.contains(it.category) }.sortedBy { it.name }.let { filteredEvents ->
                                binding.eventAutoCompleteTextView.setAdapter(
                                    BaseAdapter(
                                        binding.eventAutoCompleteTextView.context,
                                        filteredEvents.toMutableList()
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


