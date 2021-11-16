package com.drunkenboys.ckscalendar.yearcalendar

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.drunkenboys.ckscalendar.FakeFactory
import com.drunkenboys.ckscalendar.data.CalendarDesignObject
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import com.drunkenboys.ckscalendar.data.CalendarSet

class YearCalendarViewModel: ViewModel() {

    private val _schedules = mutableStateOf<List<CalendarScheduleObject>>(emptyList())
    val schedules: State<List<CalendarScheduleObject>> = _schedules

    private val _design = mutableStateOf(CalendarDesignObject())
    val design: State<CalendarDesignObject> = _design

    private val _yearList = (YearCalendarView.INIT_YEAR..YearCalendarView.LAST_YEAR).map { year ->
        FakeFactory.createFakeCalendarSetList(year)
    }
    val yearList: List<List<CalendarSet>> = _yearList

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