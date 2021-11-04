package com.drunkenboys.calendarun.ui.searchschedule.model

import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import java.util.*

data class DateItem(
    val date: Date,
    val schedule: List<Schedule>
)
