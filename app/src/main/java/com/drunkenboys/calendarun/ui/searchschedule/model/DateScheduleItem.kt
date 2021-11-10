package com.drunkenboys.calendarun.ui.searchschedule.model

import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class DateScheduleItem(val schedule: Schedule, val onClick: () -> Unit) {

    val duration: String = run {
        val startDateFormat = DateTimeFormatter.ofPattern("hh:mm")
        val endDateFormat = getEndDateFormat(schedule.startDate, schedule.endDate)

        "${schedule.startDate.format(startDateFormat)} ~ ${schedule.endDate.format(endDateFormat)}"
    }

    private fun getEndDateFormat(startDate: LocalDateTime, endDate: LocalDateTime): DateTimeFormatter {
        return when {
            startDate.year < endDate.year -> DateTimeFormatter.ofPattern("yyyy년 M월 d일 hh:mm")
            startDate.month < endDate.month -> DateTimeFormatter.ofPattern("M월 d일 hh:mm")
            startDate.dayOfYear < endDate.dayOfYear -> DateTimeFormatter.ofPattern("d일 hh:mm")
            else -> DateTimeFormatter.ofPattern("hh:mm")
        }
    }
}
