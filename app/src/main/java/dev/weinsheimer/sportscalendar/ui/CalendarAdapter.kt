package dev.weinsheimer.sportscalendar.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import dev.weinsheimer.sportscalendar.databinding.ItemCalendarEventBinding
import dev.weinsheimer.sportscalendar.databinding.ItemCalendarMonthBinding
import dev.weinsheimer.sportscalendar.domain.CalendarListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import java.util.*
import dev.weinsheimer.sportscalendar.R
import timber.log.Timber
import java.text.DateFormatSymbols
import dev.weinsheimer.sportscalendar.util.*

private const val ITEM_VIEW_TYPE_MONTH = 0
private const val ITEM_VIEW_TYPE_EVENT = 1

class CalendarAdapter(val clickListener: CalendarListener, context: Context): ListAdapter<DataItem, RecyclerView.ViewHolder>(
    CalendarDiffCallback()
) {
    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val months: List<String> by lazy {
        DateFormatSymbols(Locale.getDefault()).months.toList()
    }
    private val calendar = Calendar.getInstance()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EventViewHolder -> {
                val eventItem = getItem(holder.getAdapterPosition()) as DataItem.EventItem
                holder.bind(eventItem.event, clickListener)
            }
            is MonthViewHolder -> {
                val monthItem = getItem(position) as DataItem.MonthItem
                holder.bind(monthItem.month)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is DataItem.EventItem -> ITEM_VIEW_TYPE_EVENT
            is DataItem.MonthItem -> ITEM_VIEW_TYPE_MONTH
            else -> throw Exception("Unknown viewType!")
        }
    }

    fun add(additions: List<CalendarListItem>) {
        val result: MutableList<DataItem> = mutableListOf()
        months.forEachIndexed { index, month ->
            additions
                .filter { item ->
                    calendar.time = item.dateFrom
                    calendar.get(Calendar.MONTH) == index }
                .sortedBy { it.dateFrom }
                .let { list ->
                    if (list.isNotEmpty())
                        result.add(DataItem.MonthItem(month))
                    list.forEach { result.add(DataItem.EventItem(it)) }
                }
        }
        submitList(result)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_MONTH -> MonthViewHolder.from(parent)
            ITEM_VIEW_TYPE_EVENT -> EventViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    // private constructor -> can only be called by companion object
    class EventViewHolder private constructor(val binding: ItemCalendarEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: CalendarListItem,
            clickListener: CalendarListener
        ) {
            println("bind -> " + item.name + " -> " + item.expanded)
            binding.item = item
            binding.expanded = false
            binding.cardView.setOnClickListener {
                val expanded = binding.expanded
                binding.expanded = !expanded!!
            }
            binding.executePendingBindings()
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): EventViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCalendarEventBinding.inflate(layoutInflater, parent, false)
                return EventViewHolder(binding)
            }
        }
    }

    class MonthViewHolder private constructor(val binding: ItemCalendarMonthBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(
            month: String
        ) {
            binding.month = month
        }
        companion object {
            fun from(parent: ViewGroup): MonthViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCalendarMonthBinding.inflate(layoutInflater, parent, false)
                return MonthViewHolder(binding)
            }
        }
    }
}

class CalendarDiffCallback: DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return (oldItem.id == newItem.id && oldItem.type == oldItem.type)
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        if (oldItem is DataItem.MonthItem && newItem is DataItem.MonthItem) {
            return (oldItem.month == newItem.month)
        }
        if (oldItem is DataItem.EventItem && newItem is DataItem.EventItem) {
            return (oldItem.event == newItem.event)
        }
        return true
    }
}

class CalendarListener(val clickListener: (sport: String, eventId: Int) -> Unit) {
    fun onClick(item: CalendarListItem) = clickListener(item.sport.id, item.id)
}

sealed class DataItem {
    data class EventItem(val event: CalendarListItem): DataItem() {
        override val id = event.id
        override val type = event.sport.id
    }
    data class MonthItem(val month: String): DataItem() {
        override val id = Int.MIN_VALUE
        override val type = month
    }

    abstract val id: Int
    abstract val type: String
}
