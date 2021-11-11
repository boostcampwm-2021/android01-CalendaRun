package com.drunkenboys.ckscalendar.data

import java.time.LocalDate

data class CalendarDate(
    val date: LocalDate,
    val dayType: DayType,
    var isSelected: Boolean = false
)
