package com.drunkenboys.calendarun.ui.managecalendar.model

import androidx.recyclerview.widget.DiffUtil
import java.time.LocalDate

data class CalendarItem(
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    companion object {
        val diffUtil by lazy {
            object : DiffUtil.ItemCallback<CalendarItem>() {
                override fun areItemsTheSame(oldItem: CalendarItem, newItem: CalendarItem) = oldItem.title == newItem.title

                override fun areContentsTheSame(oldItem: CalendarItem, newItem: CalendarItem) = oldItem == newItem
            }
        }
    }
}
