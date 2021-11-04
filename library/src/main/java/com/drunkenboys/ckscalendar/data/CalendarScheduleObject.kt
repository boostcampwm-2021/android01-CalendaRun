package com.drunkenboys.ckscalendar.data

import java.time.LocalDate

// CalendarView에 스케쥴 등을 표시하기 위한 오브젝트
data class CalendarScheduleObject(
    val id: Int,
    val color: Int,
    val text: String,
    val startDate: LocalDate,
    val endDate: LocalDate
)
