package com.drunkenboys.calendarun.ui.searchschedule.model

import androidx.recyclerview.widget.DiffUtil
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.util.amPm
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

sealed class SearchScheduleItem

data class DateItem(val date: LocalDate) : SearchScheduleItem() {

    override fun toString(): String = date.format(DateTimeFormatter.ofPattern("M월 d일"))

    companion object {

        val diffUtil by lazy {
            object : DiffUtil.ItemCallback<DateItem>() {

                override fun areItemsTheSame(oldItem: DateItem, newItem: DateItem) = oldItem.hashCode() == newItem.hashCode()

                override fun areContentsTheSame(oldItem: DateItem, newItem: DateItem) = oldItem == newItem
            }
        }
    }
}

data class DateScheduleItem(val schedule: Schedule, val onClick: () -> Unit) : SearchScheduleItem() {

    override fun toString(): String {
        val startDateFormat = DateTimeFormatter.ofPattern("${schedule.startDate.amPm} hh:mm")
        val endDateFormat = getEndDateFormat(schedule.startDate, schedule.endDate)

        return "${schedule.startDate.format(startDateFormat)} ~ ${schedule.endDate.format(endDateFormat)}"
    }

    private fun getEndDateFormat(startDate: LocalDateTime, endDate: LocalDateTime): DateTimeFormatter {
        return when {
            startDate.year < endDate.year -> DateTimeFormatter.ofPattern("yyyy년 M월 d일 ${endDate.amPm} hh:mm")
            startDate.month < endDate.month -> DateTimeFormatter.ofPattern("M월 d일 ${endDate.amPm} hh:mm")
            startDate.dayOfYear < endDate.dayOfYear -> DateTimeFormatter.ofPattern("d일 ${endDate.amPm} hh:mm")
            else -> DateTimeFormatter.ofPattern("${endDate.amPm} hh:mm")
        }
    }

    companion object {

        val diffUtil by lazy {
            object : DiffUtil.ItemCallback<DateScheduleItem>() {
                override fun areItemsTheSame(oldItem: DateScheduleItem, newItem: DateScheduleItem) =
                    oldItem.schedule.id == newItem.schedule.id

                override fun areContentsTheSame(oldItem: DateScheduleItem, newItem: DateScheduleItem) = oldItem == newItem
            }
        }
    }
}
