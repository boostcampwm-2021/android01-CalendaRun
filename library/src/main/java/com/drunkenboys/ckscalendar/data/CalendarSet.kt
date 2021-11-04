package com.drunkenboys.ckscalendar.data

import java.time.LocalDate

data class CalendarSet(
    val id: Int,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
)
