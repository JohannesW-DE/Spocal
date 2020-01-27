package dev.weinsheimer.sportscalendar.util

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dev.weinsheimer.sportscalendar.R
import dev.weinsheimer.sportscalendar.domain.Athlete
import dev.weinsheimer.sportscalendar.domain.CalendarListItem
import java.text.SimpleDateFormat
import java.util.*

/**
 * Binding adapter used to show add button once an item is selected
 */
@BindingAdapter("app:visibleIfNotNull")
fun visibleIfNotNull(view: View, it: Any?) {
    view.visibility = if (it != null) View.VISIBLE else View.GONE
}


@SuppressLint("SetTextI18n")
@BindingAdapter(value = ["app:dateFrom", "app:dateTo"], requireAll = true)
fun datesToString(view: TextView, dateFrom: Date, dateTo: Date) {
    println(" --- date ---")
    println(dateFrom)
    println(dateTo)
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    sdf.isLenient = false
    if (dateFrom == dateTo) {
        view.text = sdf.format(dateFrom)
    } else {
        view.text = sdf.format(dateFrom) + "  —  " + sdf.format(dateTo)
    }

}

/**
 * item_calendar_event.xml
 */
@BindingAdapter("isVisible")
fun isVisible(view: View, expanded: Boolean) {
    view.visibility = if (expanded) View.VISIBLE else View.GONE
}


@BindingAdapter("athletes")
fun athletes(chipGroup: ChipGroup, athletes: List<Athlete>) {
    chipGroup.apply {
        removeAllViews()

        athletes.forEach { addView(Chip(chipGroup.context).createInfoChip(it.name)) }
    }
}

@BindingAdapter("item")
fun details(chipGroup: ChipGroup, item: CalendarListItem) {
    chipGroup.apply {
        removeAllViews()

        addView(Chip(chipGroup.context).createInfoChip(item.category))

        val location = if ("city" in item.details) {
            item.details["city"].toString() + ", " + item.country.name
        } else {
            item.country.name
        }
        addView(Chip(chipGroup.context).createInfoChip(location))

        item.details.forEach { detail ->
            when(detail.key) {
                "indoor" -> if (detail.value as Boolean) "Indoor" else "Outdoor"
                "surface" -> detail.value.toString()
                else -> null
            }?.let {
                addView(Chip(chipGroup.context).createInfoChip(it))
            }
        }
    }
}

@BindingAdapter("eventIcon")
fun setEventIcon(imageView: ImageView, sport: String) {
    imageView.setImageResource(
        when (sport) {
            "badminton" -> R.drawable.icon_badminton
            "cycling" -> R.drawable.icon_cycling
            "tennis" -> R.drawable.icon_tennis
            else -> R.drawable.icon_tennis
        }
    )
}

