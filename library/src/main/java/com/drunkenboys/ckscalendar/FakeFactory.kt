package com.drunkenboys.ckscalendar

import com.drunkenboys.ckscalendar.data.*
import com.drunkenboys.ckscalendar.utils.TimeUtils
import java.time.DayOfWeek
import java.time.LocalDate

object FakeFactory {

    // TODO: 나중에 디자인 요소 결정하기
    fun createFakeDesign(): CalendarDesignObject {
        val weekDayTextColor = TimeUtils.getColorInt(0, 0, 0)
        val holidayTextColor = ScheduleColorType.RED.color
        val saturdayTextColor = ScheduleColorType.BLUE.color
        val sundayTextColor = ScheduleColorType.RED.color

        return CalendarDesignObject(
            weekDayTextColor = weekDayTextColor,
            holidayTextColor = holidayTextColor,
            saturdayTextColor = saturdayTextColor,
            sundayTextColor = sundayTextColor,
            textSize = 0,
            textAlign = 16,
            selectedFrameColor = 0,
            backgroundColor = 0,
            weekSimpleStringSet = listOf(""),
            weekFullStringSet = listOf(""),
            visibleScheduleCount = 3
        )
    }

    // TODO: 달력을 생성하는 함수를 recyclerView로 옮겨서 무한 스크롤을 구현해보자!
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