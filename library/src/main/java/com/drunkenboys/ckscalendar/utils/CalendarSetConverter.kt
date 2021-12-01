package com.drunkenboys.ckscalendar.utils

import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import com.drunkenboys.ckscalendar.data.CalendarSet
import com.drunkenboys.ckscalendar.data.DayType
import java.time.DayOfWeek

fun calendarSetToCalendarDatesList(calendarSet: CalendarSet, schedules: List<CalendarScheduleObject>): List<List<CalendarDate>> {
    val holidaySchedule = schedules.filter { schedule -> schedule.isHoliday }
        .map { schedule -> schedule.startDate.toLocalDate() }
        .sortedDescending()

    // n주
    val weekList = mutableListOf<MutableList<CalendarDate>>()
    var oneDay = calendarSet.startDate
    var paddingPrev = calendarSet.startDate
    var paddingNext = calendarSet.endDate

    // 앞쪽 패딩
    if (paddingPrev.dayOfWeek != DayOfWeek.SUNDAY) weekList.add(mutableListOf())
    while (paddingPrev.dayOfWeek != DayOfWeek.SUNDAY) {
        paddingPrev = paddingPrev.minusDays(1)
        weekList.last().add(CalendarDate(paddingPrev, DayType.PADDING))
    }

    // n일 추가
    while (oneDay <= calendarSet.endDate) {

        // 일요일에는 1주일 갱신
        if (oneDay.dayOfWeek == DayOfWeek.SUNDAY) weekList.add(mutableListOf())

        // 1일 추가
        weekList.last()
            .add(CalendarDate(
                date = oneDay,
                dayType = if (holidaySchedule.any { day -> day == oneDay }) DayType.HOLIDAY
                else TimeUtils.parseDayWeekToDayType(oneDay.dayOfWeek)
            ))

        oneDay = oneDay.plusDays(1L)
    }

    // 뒤쪽 패딩
    while (paddingNext.dayOfWeek != DayOfWeek.SATURDAY) {
        paddingNext = paddingNext.plusDays(1)
        weekList.last().add(CalendarDate(paddingNext, DayType.PADDING))
    }

    return weekList
}