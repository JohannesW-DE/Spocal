package dev.weinsheimer.sportscalendar.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import dagger.android.support.DaggerFragment
import dev.weinsheimer.sportscalendar.R
import dev.weinsheimer.sportscalendar.databinding.FragmentFilterBinding
import dev.weinsheimer.sportscalendar.domain.Athlete
import dev.weinsheimer.sportscalendar.domain.Event
import dev.weinsheimer.sportscalendar.domain.EventCategory
import dev.weinsheimer.sportscalendar.util.*
import dev.weinsheimer.sportscalendar.viewmodels.SharedViewModel
import kotlinx.android.synthetic.main.filter_selection_athlete.view.*
import kotlinx.android.synthetic.main.filter_selection_event.view.*
import kotlinx.android.synthetic.main.filter_selection_event_category.view.*
import timber.log.Timber
import javax.inject.Inject


open class FilterFragment: DaggerFragment() {
    private lateinit var binding: FragmentFilterBinding

    private val selectedAthlete = MutableLiveData<Athlete>()
    private val selectedEvent = MutableLiveData<Event>()
    private val selectedCategory = MutableLiveData<EventCategory>()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val viewModel by viewModels<SharedViewModel> { viewModelFactory }

    open lateinit var sport: Sport
    open lateinit var eventName: String
    open val selectAthletes: Boolean = true
    open lateinit var athletes: LiveData<List<Athlete>>
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter, container, false)

        binding.lifecycleOwner = this

        binding.eventName = eventName

        binding.selectedAthlete = selectedAthlete
        binding.selectedEvent = selectedEvent

        if (!selectAthletes) {
            binding.athlete.root.visibility = View.GONE
        }

        /**
         * VIEW/VIEWMODEL -> ATHLETE
         */
        // populate autocompletetextview
        athletes.observe(viewLifecycleOwner, Observer { athletes ->
            binding.athlete.fsaAutoCompleteTextView.setAdapter(BaseAdapter(binding.athlete.fsaChipGroup.context, athletes))
        })

        // react to selection
        binding.athlete.fsaAutoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                selectedAthlete.value = binding.athlete.fsaAutoCompleteTextView.adapter.getItem(position) as Athlete
            }

        // react to text change, disabling adding
        binding.athlete.fsaAutoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                selectedAthlete.value = null
            }
        })

        // react to add
        binding.athlete.fsaButton.setOnClickListener {
            selectedAthlete.value?.let { athlete ->
                filteredAthletes.value?.let { athletes ->
                    athletes.add(athlete)
                    filteredAthletes.value = athletes
                }
            }
            selectedAthlete.value = null

            // try some stuff to clear focus and hide keyboard
            binding.athlete.fsaAutoCompleteTextView.setText("")
            binding.athlete.fsaAutoCompleteTextView.clearComposingText()
            binding.athlete.fsaAutoCompleteTextView.clearFocus()

            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
        }

        /**
         * VIEW/VIEWMODEL -> EVENTS
         */
        // populate spinner
        mainEventCategories.observe(viewLifecycleOwner, Observer { categories ->
            binding.event.fseSpinner.adapter =
                ArrayAdapter(
                    binding.event.fseSpinner.context,
                    R.layout.spinner_row,
                    listOf(EventCategory(0, getString(R.string.badminton_filter_categories_all), true,null)) + categories
                )
        })

        // react to spinner selection
        binding.event.fseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Timber.i("nothing")
            }
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //selectedCategory.value = binding.event.fseSpinner.adapter.getItem(position) as EventCategory
                selectedCategory.value = parent.getItemAtPosition(position) as EventCategory
                updateEventAutoComplete()
                // react to refreshed events
                binding.event.fseAutoCompleteTextView.text = binding.event.fseAutoCompleteTextView.text
                binding.event.fseAutoCompleteTextView.setSelection(binding.event.fseAutoCompleteTextView.text.length)
            }
        }

        // react to autocompletetextview selection
        binding.event.fseAutoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                selectedEvent.value = binding.event.fseAutoCompleteTextView.adapter.getItem(position) as Event
            }

        // react to text change, disabling adding
        binding.event.fseAutoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                selectedEvent.value = null
            }
        })

        // react to add
        binding.event.fseButton.setOnClickListener {
            selectedEvent.value?.let { event ->
                filteredEvents.value?.let { events ->
                    events.add(event)
                    filteredEvents.value = events
                }
            }
            selectedEvent.value = null

            // try some stuff to clear focus and hide keyboard
            binding.event.fseAutoCompleteTextView.setText("")
            binding.event.fseAutoCompleteTextView.clearComposingText()
            binding.event.fseAutoCompleteTextView.clearFocus()

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
            binding.eventCategory.fsecChipGroup.addView(
                Chip(binding.eventCategory.fsecChipGroup.context).createCheckableFilterChip(
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
                if (!binding.athlete.fsaChipGroup.contains(athlete.hashCode())) {
                    binding.athlete.fsaChipGroup.addView(
                        Chip(binding.athlete.fsaChipGroup.context).createFilterChip(
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
            binding.athlete.fsaChipGroup.removeOtherChildren(athletes.map { it.hashCode() })
            if (binding.athlete.fsaChipGroup.childCount > 0) {
                binding.athlete.fsaChipGroup.visibility = View.VISIBLE
            } else {
                binding.athlete.fsaChipGroup.visibility = View.GONE
            }
        })

        // EVENT CATEGORIES
        filteredEventCategories.observe(viewLifecycleOwner, Observer { eventCategories ->
            println("categories")
            println(eventCategories.map { it.id })
            for (index in 0 until binding.eventCategory.fsecChipGroup.childCount) {
                val chip = binding.eventCategory.fsecChipGroup.getChildAt(index) as Chip
                println(chip.id)
                if (eventCategories.map { it.hashCode() }.contains(chip.id)) {
                    chip.isChecked = true
                }
            }
        })

        // EVENTS
        filteredEvents.observe(viewLifecycleOwner, Observer { events ->
            events.forEach { event ->
                if (!binding.event.fseChipGroup.contains(event.hashCode())) {
                    binding.event.fseChipGroup.addView(
                        Chip(binding.event.fseChipGroup.context).createFilterChip(
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
            binding.event.fseChipGroup.removeOtherChildren(events.map { it.hashCode() })
            if (binding.event.fseChipGroup.childCount > 0) {
                binding.event.fseChipGroup.visibility = View.VISIBLE
            } else {
                binding.event.fseChipGroup.visibility = View.GONE
            }
        })

        return binding.root // !!!
    }

    fun updateEventAutoComplete() {
        selectedCategory.value?.let { selectedCategory ->
            if (selectedCategory.id == 0) {
                binding.event.fseAutoCompleteTextView.threshold = 1

                events.value?.let { events ->
                    events.sortedBy { it.name }.let { sortedEvents ->
                        binding.event.fseAutoCompleteTextView.setAdapter(
                            BaseAdapter(
                                binding.event.fseAutoCompleteTextView.context,
                                sortedEvents.toMutableList()
                            )
                        )
                    }
                }
            } else {
                binding.event.fseAutoCompleteTextView.threshold = 1

                eventCategories.value?.let { categories ->
                    categories.filter { it.mainCategory == selectedCategory.id }.map { it.id }.let { subCategories ->
                        events.value?.let { events ->
                            events.filter { subCategories.contains(it.category) }.sortedBy { it.name }.let { filteredEvents ->
                                binding.event.fseAutoCompleteTextView.setAdapter(
                                    BaseAdapter(
                                        binding.event.fseAutoCompleteTextView.context,
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


