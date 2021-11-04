package com.drunkenboys.calendarun.ui.searchschedule.model

import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import java.text.SimpleDateFormat
import java.util.*

data class DateItem(
    val date: Date,
    val schedule: List<Schedule>
) {

    val dateName: String = run {
        val df = SimpleDateFormat("M월 d일", Locale.getDefault())
        df.format(date)
    }
}
