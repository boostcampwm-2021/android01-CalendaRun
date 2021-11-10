package com.drunkenboys.ckscalendar

import com.drunkenboys.ckscalendar.data.*
import com.drunkenboys.ckscalendar.utils.TimeUtils
import java.time.LocalDate

object FakeFactory {

    // TODO: 나중에 디자인 요소 결정하기
    fun createFakeDesign() = CalendarDesignObject(
        weekDayTextColor = TimeUtils.getColorInt(0, 0, 0),
        holidayTextColor = ScheduleColorType.RED.color,
        saturdayTextColor = ScheduleColorType.BLUE.color,
        sundayTextColor = ScheduleColorType.RED.color,
        textSize = 10,
        textAlign = 1,
        selectedFrameColor = ScheduleColorType.MAGENTA.color,
        backgroundColor = TimeUtils.getColorInt(255, 255, 255),
        weekSimpleStringSet = listOf("일", "월", "화", "수", "목", "금", "토"),
        weekFullStringSet = listOf("일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"),
        visibleScheduleCount = 3
    )

    fun createFakeCalendarSetList(year: Int): List<CalendarSet> {
        val calendarMonth = mutableListOf<CalendarSet>()
        var startOfMonth: LocalDate
        var endOfMonth: LocalDate

        (1..12).forEach { month ->
            startOfMonth = LocalDate.of(year, month, 1)
            endOfMonth = startOfMonth.plusDays(startOfMonth.lengthOfMonth() - 1L)
            calendarMonth.add(CalendarSet(month, "${month}월", startOfMonth, endOfMonth))
        }

        return calendarMonth
    }
}