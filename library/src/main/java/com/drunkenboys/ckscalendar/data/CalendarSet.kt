package com.drunkenboys.ckscalendar.data

import android.content.Context
import com.drunkenboys.ckscalendar.R
import com.drunkenboys.ckscalendar.utils.toLong
import java.time.LocalDate

data class CalendarSet(
    val id: Int,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
) {
    companion object {

        fun generateCalendarOfYear(context: Context, year: Int): List<CalendarSet> {
            return (1..12).map { month ->
                val start = LocalDate.of(year, month, 1)
                val end = LocalDate.of(year, month, start.lengthOfMonth())
                CalendarSet(
                    id = start.toLong().toInt(),
                    name = "${year}${context.getString(R.string.common_year)} ${month}${context.getString(R.string.common_month)}",
                    startDate = start,
                    endDate = end
                )
            }
        }
    }
}
