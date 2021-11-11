package com.drunkenboys.calendarun.ui.maincalendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import com.drunkenboys.calendarun.data.checkpoint.entity.CheckPoint
import com.drunkenboys.calendarun.data.checkpoint.local.CheckPointLocalDataSource
import com.drunkenboys.calendarun.data.idstore.IdStore
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
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

    private val _scheduleList = MutableLiveData<List<CalendarScheduleObject>>()
    val scheduleList: LiveData<List<CalendarScheduleObject>> = _scheduleList

    private val _menuItemOrder = MutableLiveData<Int>()
    val menuItemOrder: LiveData<Int> = _menuItemOrder

    fun setCalendar(calendar: Calendar) {
        _calendar.value = calendar
        IdStore.putId(IdStore.KEY_CALENDAR_ID, calendar.id)
        fetchCheckPointList(calendar.id)
        fetchScheduleList(calendar.id)
    }

    fun fetchCalendarList() {
        viewModelScope.launch {
            _calendarList.value = calendarLocalDataSource.fetchAllCalendar()
        }
    }

    private fun fetchCheckPointList(calendarId: Long) {
        viewModelScope.launch {
            _checkPointList.value = checkPointLocalDataSource.fetchCalendarCheckPoints(calendarId)
        }
    }

    private fun fetchScheduleList(calendarId: Long) {
        viewModelScope.launch {
            _scheduleList.value = scheduleLocalDataSource.fetchCalendarSchedules(calendarId)
                .map { schedule -> schedule.mapToCalendarScheduleObject() }
        }
    }

    fun setMenuItemOrder(order: Int) {
        _menuItemOrder.value = order
    }

    private fun Schedule.mapToCalendarScheduleObject() = CalendarScheduleObject(
        id = id.toInt(),
        color = color,
        text = name,
        startDate = startDate,
        endDate = endDate
    )
}
