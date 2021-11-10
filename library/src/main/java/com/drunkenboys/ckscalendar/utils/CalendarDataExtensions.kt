package com.drunkenboys.ckscalendar.utils

import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.CalendarSet
import com.drunkenboys.ckscalendar.data.DayType
import java.time.DayOfWeek

fun CalendarSet.toCalendarDatesList(): List<List<CalendarDate>> {
    // n주
    val weekList = mutableListOf<MutableList<CalendarDate>>()
    var oneDay = this.startDate
    var paddingPrev = this.startDate
    var paddingNext = this.endDate

    // 앞쪽 패딩
    if (paddingPrev.dayOfWeek != DayOfWeek.SUNDAY) weekList.add(mutableListOf())
    while (paddingPrev.dayOfWeek != DayOfWeek.SUNDAY) {
        paddingPrev = paddingPrev.minusDays(1)
        weekList.last().add(CalendarDate(paddingPrev, DayType.PADDING))
    }

    // n주일 추가
    repeat(this.startDate.lengthOfMonth()) {
        // 일요일에는 1주일 갱신
        if (oneDay.dayOfWeek == DayOfWeek.SUNDAY) weekList.add(mutableListOf())

        // 1주일 추가
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