package dev.weinsheimer.sportscalendar.util

import android.annotation.SuppressLint
import android.media.Image
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dev.weinsheimer.sportscalendar.R
import dev.weinsheimer.sportscalendar.domain.Athlete
import dev.weinsheimer.sportscalendar.domain.CalendarListItem
import timber.log.Timber
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

@BindingAdapter("app:athletes")
fun entries(chipGroup: ChipGroup, athletes: List<Athlete>?) {
    chipGroup.removeAllViews()
    athletes?.forEach { athlete ->
        val chip = Chip(chipGroup.context).apply {
            text = athlete.name
        }
        chipGroup.addView(chip)
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