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

        val startYear = item.startDate.year
        val endYear = item.endDate.year

        val startMonth = item.startDate.monthValue
        val endMonth = item.endDate.monthValue
        if (startYear == endYear) {
            dates.addAll(makeMonthCells(item.startDate, item.endDate, startYear))
        } else {
            (startYear..endYear).forEach { year ->
                when (year) {
                    startYear -> {
                        val startLocalDate = LocalDate.of(year, startMonth, item.startDate.dayOfMonth)
                        val endLocalDate = if(startYear != endYear) LocalDate.of(year, 12, 31)
                        else LocalDate.of(year, item.endDate.monthValue, item.endDate.dayOfMonth)
                        dates.addAll(makeMonthCells(startLocalDate, endLocalDate, year))
                    }
                    endYear -> {
                        val startLocalDate = LocalDate.of(year, 1, 1)
                        val endLocalDate = LocalDate.of(year, endMonth, item.endDate.dayOfMonth)
                        dates.addAll(makeMonthCells(startLocalDate, endLocalDate, year))
                    }
                    else -> {
                        val startLocalDate = LocalDate.of(year, 1, 1)
                        val endLocalDate = LocalDate.of(year, 12, 31)
                        dates.addAll(makeMonthCells(startLocalDate, endLocalDate, year))
                    }
                }
            }
        }

        // add FullSize Padding
        if (dates.size < CALENDAR_FULL_SIZE) {
            val fullSizePadding = CALENDAR_FULL_SIZE - dates.size - 1
            dates.addAll(makePadding(fullSizePadding))
        }

        return dates
    }

    private fun makeMonthCells(
        startMonth: LocalDate,
        endMonth: LocalDate,
        year: Int
    ): MutableList<CalendarDate> {
        val startDay = startMonth.dayOfWeek
        val dates = mutableListOf<CalendarDate>()
        (startMonth.monthValue..endMonth.monthValue).forEach { month ->
            when (month) {
                startMonth.monthValue -> {
                    // add Start Padding
                    if (startDay != DayOfWeek.SUNDAY) {
                        dates.addAll(makePadding(startDay.ordinal))
                    }

                    // add Start Dates
                    if (startMonth.monthValue == endMonth.monthValue) {
                        dates.addAll(makeDates(startMonth.dayOfMonth, endMonth.dayOfMonth, month, year))
                    } else {
                        dates.addAll(makeDates(startMonth.dayOfMonth, startMonth.lengthOfMonth(), month, year))
                    }
                }
                endMonth.monthValue -> {
                    dates.addAll(makeDates(1, endMonth.dayOfMonth, month, year))
                }
                else -> {
                    // add Normal Dates
                    val monthDate = LocalDate.of(year, month, 1)
                    dates.addAll(makeDates(1, monthDate.lengthOfMonth(), month, year))
                }
            }
        }

        // add End Padding
        val weekPadding = 6 - dates.size % WEEK_SIZE
        dates.addAll(makePadding(weekPadding))

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
