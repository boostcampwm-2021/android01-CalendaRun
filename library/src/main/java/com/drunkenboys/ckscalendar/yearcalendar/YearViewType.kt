package com.drunkenboys.ckscalendar.yearcalendar

import com.drunkenboys.ckscalendar.data.CalendarDate

sealed class YearViewType {
    data class MonthEntity(
        val month: Int
    ) : YearViewType()

    data class WeekEntity(
        val week: List<CalendarDate>
    ) : YearViewType()
}