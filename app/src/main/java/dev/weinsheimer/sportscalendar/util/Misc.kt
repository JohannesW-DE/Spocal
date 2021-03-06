package dev.weinsheimer.sportscalendar.util

import android.view.View
import android.widget.CompoundButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.squareup.moshi.JsonDataException
import dev.weinsheimer.sportscalendar.R
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.*

enum class Sport(val id: String) {
    BADMINTON("badminton"),
    TENNIS("tennis"),
    CYCLING("cycling")
}

object Constants {
    const val APP_NAME = "spocal"
    const val TABLE_USER_ATTRIBUTE_DATA = "data"
}

private val sdf = SimpleDateFormat("yyyy-MM-dd - HH:mm:ss", Locale.US)
private val simpleSdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

fun String.asDate(): Date {
    return sdf.parse(this)
}

fun Date.asString(): String {
    return sdf.format(this)
}

fun String.asSimpleDate(): Date {
    return simpleSdf.parse(this)
}

fun ChipGroup.contains(id: Int): Boolean {
    for(index in 0 until this.childCount) {
        if (this.getChildAt(index).id == id)
            return true
    }
    return false
}

fun ChipGroup.removeOtherChildren(ids: List<Int>) {
    println(ids)
    val indexes = mutableListOf<Int>()
    println(this.childCount)
    for (index in 0 until this.childCount) {
        if (!ids.contains(this.getChildAt(index).id)) {
            indexes.add(index)
        }
    }
    for (index in indexes.reversed()) {
        this.removeViewAt(index)
    }
}

fun Chip.createFilterChip(txt: String, listener: View.OnClickListener, obj: Any): Chip {
    return Chip(this.context).apply {
        // basics
        id = obj.hashCode()
        println("id = $id")
        text = txt
        //
        isClickable = false
        closeIcon = context.getDrawable(R.drawable.icon_close)
        isCloseIconVisible = true
        setOnCloseIconClickListener(listener)
        // style
        setChipBackgroundColorResource(
            context.resources.getIdentifier("colorPrimaryLight", "color", context.packageName))
        setTextAppearanceResource(R.style.FilterChipText)
    }
}

fun Chip.createCheckableFilterChip(txt: String, listener: CompoundButton.OnCheckedChangeListener, obj: Any): Chip {
    return Chip(context).apply {
        // basics
        id = obj.hashCode()
        text = txt
        //
        isClickable = true
        isCheckable = true
        isChecked = false
        setOnCheckedChangeListener(listener)
        // style
        setChipBackgroundColorResource(
            context.resources.getIdentifier("colorPrimaryLight", "color", context.packageName))
        setTextAppearanceResource(R.style.FilterChipText)
    }
}

fun Chip.createInfoChip(txt: String): Chip {
    return Chip(context).apply {
        // basics
        text = txt
        //
        isClickable = false
        isCheckable = false
        // style
        setChipBackgroundColorResource(
            context.resources.getIdentifier("colorPrimaryLight", "color", context.packageName))
        setTextAppearanceResource(R.style.FilterChipText)
        chipStartPadding = 0f
        chipEndPadding = 0f
        chipMinHeight = 0f
        chipCornerRadius = 12f
        val scale = resources.displayMetrics.density
        setPadding(0, (5 * scale + 0.5f).toInt(), 0, (5 * scale + 0.5f).toInt() )
    }
}

enum class RefreshExceptionType(val id: String) {
    CONNECTION("connection"),
    CODE("code"),
    FORMAT("format")
}

class RefreshException(type: RefreshExceptionType): Exception(type.id)

suspend fun refresh(function: suspend () -> Unit) {
    try {
        function()
    } catch (e: SocketTimeoutException) {
        throw RefreshException(RefreshExceptionType.CONNECTION)
    } catch (e: JsonDataException) {
        throw RefreshException(RefreshExceptionType.FORMAT)
    } catch (e: RefreshException) {
        throw e
    }
}