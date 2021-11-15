package com.drunkenboys.calendarun.ui.managecalendar.model

import java.time.LocalDate

data class CalendarItem(
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate
)
