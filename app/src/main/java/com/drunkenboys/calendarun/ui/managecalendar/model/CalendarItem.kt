package com.drunkenboys.calendarun.ui.managecalendar.model

import androidx.recyclerview.widget.DiffUtil
import com.drunkenboys.calendarun.data.calendar.entity.Calendar

data class CalendarItem(
    val id: Long,
    val name: String,
    var check: Boolean = false
) {
    fun toCalendar() = Calendar(
        id = id,
        name = name,
    )

    companion object {
        val diffUtil by lazy {
            object : DiffUtil.ItemCallback<CalendarItem>() {
                override fun areItemsTheSame(oldItem: CalendarItem, newItem: CalendarItem) = oldItem.id == newItem.id

                override fun areContentsTheSame(oldItem: CalendarItem, newItem: CalendarItem) = oldItem == newItem
            }
        }
    }
}

