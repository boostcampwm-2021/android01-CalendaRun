package com.drunkenboys.ckscalendar.yearcalendar

import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import com.drunkenboys.ckscalendar.utils.TimeUtils.dayValue
import com.drunkenboys.ckscalendar.utils.TimeUtils.isSameWeek
import java.time.DayOfWeek
import java.time.LocalDate

class YearCalendarController {

    fun isFirstWeek(week: List<CalendarDate>, monthId: Int) = week.any { day ->
        day.date.dayOfMonth == 1 && monthId == day.date.monthValue
    }

    fun getStartScheduleList(today: LocalDate, scheduleList: List<CalendarScheduleObject>) = scheduleList.filter { schedule ->
        val isStart = schedule.startDate.toLocalDate() == today
        val isSunday = today.dayOfWeek == DayOfWeek.SUNDAY
        val isFirstOfMonth = today.dayOfMonth == 1
        val isDateInScheduleRange = today in schedule.startDate.toLocalDate()..schedule.endDate.toLocalDate()
        isStart || ((isSunday || isFirstOfMonth) && (isDateInScheduleRange))
    }

    fun setWeekSchedule(
        todaySchedules: List<CalendarScheduleObject>,
        weekSchedules: Array<Array<CalendarScheduleObject?>>,
        today: LocalDate
    ) {
        val todayOfWeek = today.dayOfWeek.dayValue()

        todaySchedules.forEach { todaySchedule ->
            val weekEndDate =
                if (!today.isSameWeek(todaySchedule.endDate.toLocalDate())) DayOfWeek.SATURDAY.value
                else todaySchedule.endDate.dayOfWeek.dayValue()

            weekSchedules[todayOfWeek].forEachIndexed { index, space ->
                if (space == null) {
                    (todayOfWeek..weekEndDate).forEach { weekNum ->
                        weekSchedules[weekNum][index] = todaySchedule
                    }
                    return@forEach
                }
            }
        }
    }
}