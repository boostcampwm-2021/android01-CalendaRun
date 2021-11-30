package com.drunkenboys.calendarun.ui.managecalendar.model

import androidx.recyclerview.widget.DiffUtil
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import java.time.LocalDate

data class CalendarItem(
    val id: Long,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    var check: Boolean = false
) {

    fun toCalendar() = Calendar(
        id = id,
        name = name,
        startDate = startDate,
        endDate = endDate
    )

    companion object {

        val diffUtil by lazy {
            object : DiffUtil.ItemCallback<CalendarItem>() {
                override fun areItemsTheSame(oldItem: CalendarItem, newItem: CalendarItem) = oldItem.id == newItem.id

                override fun areContentsTheSame(oldItem: CalendarItem, newItem: CalendarItem) = oldItem == newItem
            }
        }

        fun from(calendar: Calendar) = CalendarItem(
            id = calendar.id,
            name = calendar.name,
            startDate = calendar.startDate,
            endDate = calendar.endDate
        )
    }
}
