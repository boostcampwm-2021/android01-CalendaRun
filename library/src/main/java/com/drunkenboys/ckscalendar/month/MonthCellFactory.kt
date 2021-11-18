package com.drunkenboys.ckscalendar.month

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
                    dates.addAll(makeDates(item.startDate, month))
                }
                else -> {
                    // add Normal Dates
                    dates.addAll(makeDates(item.endDate, month))
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

    private fun makeDates(selectedDate: LocalDate, month: Int): List<CalendarDate> {
        val monthDate = LocalDate.of(selectedDate.year, month, 1)
        return (1..monthDate.lengthOfMonth()).map { day ->
            val date = LocalDate.of(selectedDate.year, month, day)
            val dayType = TimeUtils.parseDayWeekToDayType(date.dayOfWeek)
            CalendarDate(date, dayType)
        }
    }

    private fun makePadding(paddingCount: Int): List<CalendarDate> {
        return (0..paddingCount).map {
            CalendarDate(LocalDate.now(), DayType.PADDING)
        }
    }

    companion object{

        private const val WEEK_SIZE = 7
        private const val CALENDAR_FULL_SIZE = 42
    }
}
