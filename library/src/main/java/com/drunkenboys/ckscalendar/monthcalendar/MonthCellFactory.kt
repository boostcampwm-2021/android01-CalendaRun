package com.drunkenboys.ckscalendar.monthcalendar

import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.CalendarSet
import com.drunkenboys.ckscalendar.data.DayType
import com.drunkenboys.ckscalendar.utils.TimeUtils
import java.time.DayOfWeek
import java.time.LocalDate

class MonthCellFactory {

    fun makeCell(item: CalendarSet): MutableList<CalendarDate> {
        val dates = mutableListOf<CalendarDate>()
        val startMonth = item.startDate.monthValue
        val startDay = item.startDate.dayOfWeek
        val endMonth = item.endDate.monthValue
        (startMonth..endMonth).forEach { month ->
            when (month) {
                startMonth -> {
                    // add Start Padding
                    if (startDay != DayOfWeek.SUNDAY) {
                        dates.addAll(makePadding(startDay.ordinal))
                    }

                    // add Start Dates
                    if (startMonth == endMonth) {
                        dates.addAll(makeDates(item.startDate.dayOfMonth, item.endDate.dayOfMonth, month, item.startDate.year))
                    } else {
                        dates.addAll(makeDates(item.startDate.dayOfMonth, item.startDate.lengthOfMonth(), month, item.startDate.year))
                    }
                }
                endMonth -> {
                    dates.addAll(makeDates(1, item.endDate.dayOfMonth, month, item.endDate.year))
                }
                else -> {
                    // add Normal Dates
                    val monthDate = LocalDate.of(item.endDate.year, month, 1)
                    dates.addAll(makeDates(1, monthDate.lengthOfMonth(), month, monthDate.year))
                }
            }
        }
        // add End Padding
        val weekPadding = 6 - dates.size % WEEK_SIZE
        dates.addAll(makePadding(weekPadding))

        // add FullSize Padding
        if (dates.size < CALENDAR_FULL_SIZE) {
            val fullSizePadding = CALENDAR_FULL_SIZE - dates.size - 1
            dates.addAll(makePadding(fullSizePadding))
        }

        return dates
    }

    private fun makeDates(startDay: Int, endDay: Int, month: Int, year: Int): List<CalendarDate> {
        return (startDay..endDay).map { day ->
            val date = LocalDate.of(year, month, day)
            val dayType = TimeUtils.parseDayWeekToDayType(date.dayOfWeek)
            CalendarDate(date, dayType)
        }
    }

    private fun makePadding(paddingCount: Int): List<CalendarDate> {
        return (0..paddingCount).map {
            // TODO: 기준 날짜를 추가 해 의미있는 날짜 주입 필요
            CalendarDate(LocalDate.of(1970, 1, 1), DayType.PADDING)
        }
    }

    companion object {

        private const val WEEK_SIZE = 7
        private const val CALENDAR_FULL_SIZE = 42
    }
}
