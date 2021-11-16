package com.drunkenboys.ckscalendar.yearcalendar

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.drunkenboys.ckscalendar.data.CalendarDesignObject
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject

class YearCalendarViewModel: ViewModel() {

    private val _schedules = mutableStateOf<List<CalendarScheduleObject>>(emptyList())
    val schedules: State<List<CalendarScheduleObject>> = _schedules

    private val _design = mutableStateOf(CalendarDesignObject())
    val design: State<CalendarDesignObject> = _design

    fun setSchedule(schedule: CalendarScheduleObject) {
        this._schedules.value = this.schedules.value.plus(schedule)
    }

    fun setSchedules(schedules: List<CalendarScheduleObject>) {
        this._schedules.value = schedules
    }

    fun setDesign(design: CalendarDesignObject) {
        _design.value = design
    }

    fun resetDesign() {
        _design.value = CalendarDesignObject()
    }
}