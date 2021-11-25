package com.drunkenboys.calendarun.ui.searchschedule.model

import androidx.recyclerview.widget.DiffUtil
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.util.amPm
import com.drunkenboys.calendarun.util.relativeDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

sealed class DateScheduleItem {

    companion object {

        val diffUtil by lazy {
            object : DiffUtil.ItemCallback<DateScheduleItem>() {

                override fun areItemsTheSame(oldItem: DateScheduleItem, newItem: DateScheduleItem) =
                    oldItem.hashCode() == newItem.hashCode()

                override fun areContentsTheSame(oldItem: DateScheduleItem, newItem: DateScheduleItem) = oldItem == newItem
            }
        }
    }
}

data class DateItem(val date: LocalDate) : DateScheduleItem() {

    override fun toString(): String = date.format(relativeDateFormat(LocalDate.now(), date))
}

data class ScheduleItem(val schedule: Schedule, val onClick: () -> Unit) : DateScheduleItem() {

    override fun toString(): String {
        val startDateFormat = DateTimeFormatter.ofPattern("${schedule.startDate.amPm} hh:mm")
        val endDateFormat = relativeDateFormat(schedule.startDate, schedule.endDate)

        return "${schedule.startDate.format(startDateFormat)} ~ ${schedule.endDate.format(endDateFormat)}"
    }

    fun toString(baseDateTime: LocalDateTime?): String {
        baseDateTime ?: return toString()

        val startFormat = relativeDateFormat(baseDateTime, schedule.startDate)
        val endFormat = relativeDateFormat(baseDateTime, schedule.endDate)

        return "${schedule.startDate.format(startFormat)} ~ ${schedule.endDate.format(endFormat)}"
    }

    companion object {

        val diffUtil by lazy {
            object : DiffUtil.ItemCallback<ScheduleItem>() {
                override fun areItemsTheSame(oldItem: ScheduleItem, newItem: ScheduleItem) =
                    oldItem.schedule.id == newItem.schedule.id

                override fun areContentsTheSame(oldItem: ScheduleItem, newItem: ScheduleItem) = oldItem == newItem
            }
        }
    }
}
