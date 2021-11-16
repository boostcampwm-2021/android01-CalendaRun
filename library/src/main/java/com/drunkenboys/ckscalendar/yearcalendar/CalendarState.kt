package com.drunkenboys.ckscalendar.yearcalendar

import androidx.compose.runtime.*
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject

class CalendarState {

    private val _schedules = mutableStateOf<List<CalendarScheduleObject>>(emptyList())
    val schedules: State<List<CalendarScheduleObject>> = _schedules

    fun setSchedule(schedule: CalendarScheduleObject) {
        this._schedules.value = this.schedules.value.plus(schedule)
    }

    fun setSchedules(schedules: List<CalendarScheduleObject>) {
        this._schedules.value = schedules
    }
}