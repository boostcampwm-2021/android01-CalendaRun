package com.drunkenboys.calendarun.ui.searchschedule.model

import androidx.recyclerview.widget.DiffUtil
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DateItem(
    val date: LocalDate,
    val scheduleList: List<DateScheduleItem>
) {

    val dateName: String = date.format(DateTimeFormatter.ofPattern("M월 d일"))

    companion object {

        val diffUtil = object : DiffUtil.ItemCallback<DateItem>() {

            override fun areItemsTheSame(oldItem: DateItem, newItem: DateItem) = oldItem.hashCode() == newItem.hashCode()

            override fun areContentsTheSame(oldItem: DateItem, newItem: DateItem) = oldItem == newItem
        }
    }
}
