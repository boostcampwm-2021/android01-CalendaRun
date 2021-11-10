package com.drunkenboys.calendarun.ui.searchschedule.model

import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.util.localDateTimeToDate
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import java.util.Calendar.*

data class DateScheduleItem(val schedule: Schedule, val onClick: () -> Unit) {

    val duration: String = run {
        val startDateFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
        val endDateFormat = getEndDateFormat(schedule.startDate, schedule.endDate)

        "${startDateFormat.format(schedule.startDate)} ~ ${endDateFormat.format(schedule.endDate)}"
    }

    private fun getEndDateFormat(startDate: LocalDateTime, endDate: LocalDateTime): SimpleDateFormat {
        val startDateCal = getInstance().apply { time = localDateTimeToDate(startDate) }
        val endDateCal = getInstance().apply { time = localDateTimeToDate(endDate) }

        return when {
            startDateCal.get(YEAR) < endDateCal.get(YEAR) -> SimpleDateFormat("yyyy년 M월 d일 hh:mm", Locale.getDefault())
            startDateCal.get(MONTH) < endDateCal.get(MONTH) -> SimpleDateFormat("M월 d일 hh:mm", Locale.getDefault())
            startDateCal.get(DAY_OF_YEAR) < endDateCal.get(DAY_OF_YEAR) -> SimpleDateFormat("d일 hh:mm", Locale.getDefault())
            else -> SimpleDateFormat("hh:mm", Locale.getDefault())
        }
    }
}
