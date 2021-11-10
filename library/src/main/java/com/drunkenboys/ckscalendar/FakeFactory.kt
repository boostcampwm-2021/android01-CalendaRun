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

    fun createFakeSchedule(): List<CalendarScheduleObject> {
        val today = LocalDate.now()

        return listOf(
            CalendarScheduleObject(
                id = 0,
                color = ScheduleColorType.YELLOW.color,
                text = "옛스케줄옛옛",
                startDate = today.minusDays(20),
                endDate = today.minusDays(5)
            ),
            CalendarScheduleObject(
                id = 1,
                color = ScheduleColorType.YELLOW.color,
                text = "뒷스케줄뒷뒷",
                startDate = today.plusDays(2),
                endDate = today.plusDays(7)
            ),
            CalendarScheduleObject(
                id = 2,
                color = ScheduleColorType.BLUE.color,
                text = "앞스케줄앞앞",
                startDate = today.minusDays(7),
                endDate = today.minusDays(2)
            ),
            CalendarScheduleObject(
                id = 3,
                color = ScheduleColorType.MAGENTA.color,
                text = "깍두기깍두기",
                startDate = today.minusDays(5),
                endDate = today.plusDays(5)
            ),
            CalendarScheduleObject(
                id = 4,
                color = ScheduleColorType.CYAN.color,
                text = "긴스케줄긴긴",
                startDate = today.minusDays(9),
                endDate = today.plusDays(9)
            )
        )
    }
}