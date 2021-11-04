package com.drunkenboys.calendarun.ui.searchschedule.model

import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import java.text.SimpleDateFormat
import java.util.*

data class DateScheduleItem(val schedule: Schedule) {

    val duration: String = run {
        val startDateFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
        val endDateFormat = getEndDateFormat(schedule.startDate, schedule.endDate)

        "${startDateFormat.format(schedule.startDate)} ~ ${endDateFormat.format(schedule.endDate)}"
    }

    private fun getEndDateFormat(startDate: Date, endDate: Date): SimpleDateFormat {
        val startDateCal = Calendar.getInstance().apply { time = startDate }
        val endDateCal = Calendar.getInstance().apply { time = endDate }

        return when {
            startDateCal.get(Calendar.YEAR) < endDateCal.get(Calendar.YEAR) -> SimpleDateFormat("yy년 m월 d일 hh:mm", Locale.getDefault())
            startDateCal.get(Calendar.MONTH) < endDateCal.get(Calendar.MONTH) -> SimpleDateFormat("m월 d일 hh:mm", Locale.getDefault())
            startDateCal.get(Calendar.DAY_OF_YEAR) < endDateCal.get(Calendar.DAY_OF_YEAR) ->
                SimpleDateFormat("d일 hh:mm", Locale.getDefault())
            else -> SimpleDateFormat("hh:mm", Locale.getDefault())
        }
    }
}
