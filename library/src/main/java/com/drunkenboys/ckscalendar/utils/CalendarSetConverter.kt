package com.drunkenboys.ckscalendar.utils

import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.CalendarSet
import com.drunkenboys.ckscalendar.data.DayType
import com.drunkenboys.ckscalendar.utils.TimeUtils.dayValue
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun calendarSetToCalendarDatesList(calendarSet: CalendarSet): List<List<CalendarDate>> {
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
        weekList.last().add(CalendarDate(oneDay, TimeUtils.parseDayWeekToDayType(oneDay.dayOfWeek)))

        oneDay = oneDay.plusDays(1L)
    }

    // 뒤쪽 패딩
    while (paddingNext.dayOfWeek != DayOfWeek.SATURDAY) {
        paddingNext = paddingNext.plusDays(1)
        weekList.last().add(CalendarDate(paddingNext, DayType.PADDING))
    }

    return weekList
}