package com.drunkenboys.calendarun.ui.maincalendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import com.drunkenboys.calendarun.data.checkpoint.entity.CheckPoint
import com.drunkenboys.calendarun.data.checkpoint.local.CheckPointLocalDataSource
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCalendarViewModel @Inject constructor(
    private val calendarLocalDataSource: CalendarLocalDataSource,
    private val checkPointLocalDataSource: CheckPointLocalDataSource,
    private val scheduleLocalDataSource: ScheduleLocalDataSource
) : ViewModel() {

    private val _calendar = MutableLiveData<Calendar>()
    val calendar: LiveData<Calendar> = _calendar

    private val _calendarList = MutableLiveData<List<Calendar>>()
    val calendarList: LiveData<List<Calendar>> = _calendarList

    private val _checkPointList = MutableLiveData<List<CheckPoint>>()
    val checkPointList: LiveData<List<CheckPoint>> = _checkPointList

    private val _scheduleList = MutableLiveData<List<Schedule>>()
    val scheduleList: LiveData<List<Schedule>> = _scheduleList

    fun setCalendar(calendar: Calendar) {
        _calendar.value = calendar
        fetchCheckPointList(calendar.id)
        fetchScheduleList(calendar.id)
    }

    fun fetchCalendarList() {
        viewModelScope.launch {
            _calendarList.value = calendarLocalDataSource.fetchAllCalendar()
        }
    }

    fun fetchCheckPointList(calendarId: Long) {
        viewModelScope.launch {
            _checkPointList.value = checkPointLocalDataSource.fetchCalendarCheckPoints(calendarId)
        }
    }

    fun fetchScheduleList(calendarId: Long) {
        viewModelScope.launch {
            _scheduleList.value = scheduleLocalDataSource.fetchCalendarSchedules(calendarId)
        }
    }
}
