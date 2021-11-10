package com.drunkenboys.calendarun.ui.searchschedule.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DateItem(
    val date: LocalDate,
    val scheduleList: List<DateScheduleItem>
) {

    val dateName: String = date.format(DateTimeFormatter.ofPattern("M월 d일"))
}
