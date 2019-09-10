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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import dev.weinsheimer.sportscalendar.R
import dev.weinsheimer.sportscalendar.domain.Athlete
import dev.weinsheimer.sportscalendar.domain.Event
import dev.weinsheimer.sportscalendar.domain.EventCategory
import dev.weinsheimer.sportscalendar.util.*
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import timber.log.Timber
import android.os.HandlerThread
import android.os.Handler
import dev.weinsheimer.sportscalendar.databinding.FragmentBadmintonFilterBinding
import kotlinx.android.synthetic.main.filter_selection_event_category.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


open class FilterFragment: Fragment() {
    private lateinit var binding: FragmentBadmintonFilterBinding

    private val selectedAthlete = MutableLiveData<Athlete>()
    private val selectedEvent = MutableLiveData<Event>()
    private val selectedCategory = MutableLiveData<EventCategory>()

    private val viewModel by sharedViewModel<SharedViewModel>()
    open lateinit var sport: String
    open val selectAthletes: Boolean = true
    open lateinit var athletes: LiveData<List<Athlete>>
    open val selectEvents: Boolean = true
    open lateinit var events: LiveData<List<Event>>
    open lateinit var mainEventCategories: LiveData<List<EventCategory>>
    open lateinit var eventCategories: LiveData<List<EventCategory>>

    private val filteredAthletes = MutableLiveData<MutableSet<Athlete>>()
    private val filteredEventCategories = MutableLiveData<MutableSet<EventCategory>>()
    private val filteredEvents = MutableLiveData<MutableSet<Event>>()

    override fun onResume() {
        super.onResume()
        filteredAthletes.value = athletes.value?.filter { it.filter }?.toMutableSet()
        filteredEventCategories.value = eventCategories.value?.filter { it.filter }?.toMutableSet()
        filteredEvents.value = events.value?.filter { it.filter }?.toMutableSet()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_badminton_filter, container, false)

        binding.lifecycleOwner = this

        binding.selectedAthlete = selectedAthlete
        binding.selectedEvent = selectedEvent

        if (!selectAthletes) {
            binding.athlete.root.visibility = View.GONE
        }
        if (!selectEvents) {
            binding.event.root.visibility = View.GONE
        }

        /**
         * VIEW/VIEWMODEL -> ATHLETE
         */
        // populate autocompletetextview
        athletes.observe(viewLifecycleOwner, Observer { athletes ->
            binding.athlete.athleteAutoCompleteTextView.setAdapter(BaseAdapter(binding.athlete.athleteFilterChipGroup.context, athletes))
        })

        // react to selection
        binding.athlete.athleteAutoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                selectedAthlete.value = binding.athlete.athleteAutoCompleteTextView.adapter.getItem(position) as Athlete
            }

        // react to text change, disabling adding
        binding.athlete.athleteAutoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                selectedAthlete.value = null
            }
        })

        // react to add
        binding.athlete.button.setOnClickListener {
            selectedAthlete.value?.let { athlete ->
                filteredAthletes.value?.let { athletes ->
                    athletes.add(athlete)
                    filteredAthletes.value = athletes
                }
            }
            selectedAthlete.value = null

            // try some stuff to clear focus and hide keyboard
            binding.athlete.athleteAutoCompleteTextView.setText("")
            binding.athlete.athleteAutoCompleteTextView.clearComposingText()
            binding.athlete.athleteAutoCompleteTextView.clearFocus()

            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
        }

        /**
         * VIEW/VIEWMODEL -> EVENTS
         */
        // populate spinner
        mainEventCategories.observe(viewLifecycleOwner, Observer { categories ->
            binding.event.spinner.adapter =
                ArrayAdapter(
                    binding.event.spinner.context,
                    R.layout.spinner_row,
                    listOf(EventCategory(0, getString(R.string.badminton_filter_categories_all), true,null)) + categories
                )
        })

        // react to spinner selection
        binding.event.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Timber.i("nothing")
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory.value = binding.event.spinner.adapter.getItem(position) as EventCategory
                updateEventAutoComplete()
                // react to refreshed events
                binding.event.eventAutoCompleteTextView.text = binding.event.eventAutoCompleteTextView.text
                binding.event.eventAutoCompleteTextView.setSelection(binding.event.eventAutoCompleteTextView.text.length)
            }
        }

        // react to autocompletetextview selection
        binding.event.eventAutoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                selectedEvent.value = binding.event.eventAutoCompleteTextView.adapter.getItem(position) as Event
            }

        // react to text change, disabling adding
        binding.event.eventAutoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                selectedEvent.value = null
            }
        })

        // react to add
        binding.event.button.setOnClickListener {
            selectedEvent.value?.let { event ->
                filteredEvents.value?.let { events ->
                    events.add(event)
                    filteredEvents.value = events
                }
            }
            selectedEvent.value = null

            // try some stuff to clear focus and hide keyboard
            binding.event.eventAutoCompleteTextView.setText("")
            binding.event.eventAutoCompleteTextView.clearComposingText()
            binding.event.eventAutoCompleteTextView.clearFocus()

            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
        }

        /**
         * VIEW/VIEWMODEL -> EVENT CATEGORIES
         */
        val handlerThread = HandlerThread("FilterFragmentChipHandler")
        handlerThread.start()
        val looper = handlerThread.looper
        val handler = Handler(looper)
        //handler.post {}
        eventCategories.value?.forEach { category ->
            binding.eventCategory.eventCategoryFilterChipGroup.addView(
                Chip(binding.eventCategory.eventCategoryFilterChipGroup.context).createCheckableFilterChip(
                    category.name,
                    CompoundButton.OnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            filteredEventCategories.value?.add(category)
                        } else {
                            filteredEventCategories.value?.remove(category)
                        }
                    },
                    category
                )
            )
        }

        /**
         * FAB
         */
        binding.floatingActionButton.setOnClickListener {
            viewModel.updateFilters(
                sport, filteredAthletes.value?.toList(), filteredEventCategories.value?.toList(), filteredEvents.value?.toList()
            )
        }

        /**
         * Filter observation
         */
        // ATHLETES
        filteredAthletes.observe(viewLifecycleOwner, Observer { athletes ->
            athletes.forEach { athlete ->
                if (!binding.athlete.athleteFilterChipGroup.contains(athlete.id)) {
                    binding.athlete.athleteFilterChipGroup.addView(
                        Chip(binding.athlete.athleteFilterChipGroup.context).createFilterChip(
                            athlete.name,
                            View.OnClickListener {
                                filteredAthletes.value = filteredAthletes.value?.apply {
                                    remove(athlete)
                                }
                            },
                            athlete
                        )
                    )
                }
            }
            binding.athlete.athleteFilterChipGroup.removeOtherChildren(athletes.map { it.id })
            if (binding.athlete.athleteFilterChipGroup.childCount > 0) {
                binding.athlete.athleteFilterChipGroup.visibility = View.VISIBLE
            } else {
                binding.athlete.athleteFilterChipGroup.visibility = View.GONE
            }
        })

        // EVENT CATEGORIES
        filteredEventCategories.observe(viewLifecycleOwner, Observer { eventCategories ->
            for (index in 0 until binding.eventCategory.eventCategoryFilterChipGroup.childCount) {
                val chip = binding.eventCategory.eventCategoryFilterChipGroup.getChildAt(index) as Chip
                if (eventCategories.map { it.id }.contains(chip.id)) {
                    chip.isChecked = true
                }
            }
        })

        // EVENTS
        filteredEvents.observe(viewLifecycleOwner, Observer { events ->
            events.forEach { event ->
                if (!binding.event.eventFilterChipGroup.contains(event.id)) {
                    binding.event.eventFilterChipGroup.addView(
                        Chip(binding.event.eventFilterChipGroup.context).createFilterChip(
                            event.name,
                            View.OnClickListener {
                                filteredEvents.value = filteredEvents.value?.apply {
                                    remove(event)
                                }
                            },
                            event
                        )
                    )
                }
            }
            binding.event.eventFilterChipGroup.removeOtherChildren(events.map { it.id })
            if (binding.event.eventFilterChipGroup.childCount > 0) {
                binding.event.eventFilterChipGroup.visibility = View.VISIBLE
            } else {
                binding.event.eventFilterChipGroup.visibility = View.GONE
            }
        })

        return binding.root // !!!
    }

    fun updateEventAutoComplete() {
        selectedCategory.value?.let { selectedCategory ->
            if (selectedCategory.id == 0) {
                binding.event.eventAutoCompleteTextView.threshold = 1

                events.value?.let { events ->
                    events.sortedBy { it.name }.let { sortedEvents ->
                        binding.event.eventAutoCompleteTextView.setAdapter(
                            BaseAdapter(
                                binding.event.eventAutoCompleteTextView.context,
                                sortedEvents.toMutableList()
                            )
                        )
                    }
                }
            } else {
                binding.event.eventAutoCompleteTextView.threshold = 1

                eventCategories.value?.let { categories ->
                    println(categories)
                    categories.filter { it.mainCategory == selectedCategory.id }.map { it.id }.let { subCategories ->
                        events.value?.let { events ->
                            println(events)
                            events.filter { subCategories.contains(it.category) }.sortedBy { it.name }.let { filteredEvents ->
                                println(filteredEvents)
                                binding.event.eventAutoCompleteTextView.setAdapter(
                                    BaseAdapter(
                                        binding.event.eventAutoCompleteTextView.context,
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


