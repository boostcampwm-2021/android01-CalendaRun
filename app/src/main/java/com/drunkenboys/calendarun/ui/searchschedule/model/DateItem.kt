package com.drunkenboys.calendarun.ui.searchschedule.model

import java.text.SimpleDateFormat
import java.util.*

data class DateItem(
    val date: Date,
    val scheduleList: List<DateScheduleItem>
) {

    val dateName: String = run {
        val df = SimpleDateFormat("M월 d일", Locale.getDefault())
        df.format(date)
    }
}
