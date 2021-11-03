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
            weekDayTextColor,
            holidayTextColor,
            saturdayTextColor,
            sundayTextColor,
            0,
            16,
            0,
            0,
            listOf(""),
            listOf(""),
            3
        )
    }

    // TODO: 달력을 생성하는 함수를 recyclerView로 옮겨서 무한 스크롤을 구현해보자!
    fun createFakeMonth(year: Int, month: Int): List<List<CalendarDate>> {
        var today = LocalDate.of(year, month, 1)
        val calendarMonth = mutableListOf<MutableList<CalendarDate>>()
        val calendarWeek = mutableListOf<CalendarDate>()

        repeat(today.lengthOfMonth()) {
            calendarWeek.add(CalendarDate(today, DayType.SUNDAY))

            if (today.dayOfWeek == DayOfWeek.SATURDAY) {
                val week = mutableListOf<CalendarDate>()

                // TODO 이전달 패딩
//                while (calendarWeek.size < 7) week.add(CalendarDate(today, DayType.PADDING))
                week.addAll(calendarWeek)

                calendarMonth.add(week)
                calendarWeek.clear()
            }

            today = today.plusDays(1)
        }

        // TODO 다음달 패딩
        if (calendarWeek.isNotEmpty()) calendarMonth.add(calendarWeek)

        return calendarMonth
    }
}