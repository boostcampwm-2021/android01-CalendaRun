package com.drunkenboys.calendarun.ui.maincalendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import com.drunkenboys.calendarun.data.calendartheme.local.CalendarThemeLocalDataSource
import com.drunkenboys.calendarun.data.checkpoint.entity.CheckPoint
import com.drunkenboys.calendarun.data.checkpoint.local.CheckPointLocalDataSource
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.ui.theme.toCalendarDesignObject
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainCalendarViewModel @Inject constructor(
    private val calendarLocalDataSource: CalendarLocalDataSource,
    private val checkPointLocalDataSource: CheckPointLocalDataSource,
    private val scheduleLocalDataSource: ScheduleLocalDataSource,
    private val calendarThemeDataSource: CalendarThemeLocalDataSource
) : ViewModel() {

    private val _calendar = MutableStateFlow<Calendar?>(null)
    val calendar: StateFlow<Calendar?> = _calendar

    private val _calendarList = MutableStateFlow<List<Calendar>>(emptyList())
    val calendarList: StateFlow<List<Calendar>> = _calendarList

    private val _checkPointList = MutableStateFlow<List<CheckPoint>>(emptyList())
    val checkPointList: StateFlow<List<CheckPoint>> = _checkPointList

    private val _scheduleList = MutableStateFlow<List<CalendarScheduleObject>>(emptyList())
    val scheduleList: StateFlow<List<CalendarScheduleObject>> = _scheduleList

    private val _menuItemOrder = MutableStateFlow(0)
    val menuItemOrder: StateFlow<Int> = _menuItemOrder

    private val _fabClickEvent = MutableSharedFlow<Long>()
    val fabClickEvent: SharedFlow<Long> = _fabClickEvent

    private val _daySecondClickEvent = MutableSharedFlow<LocalDate>()
    val daySecondClickEvent: SharedFlow<LocalDate> = _daySecondClickEvent

    fun setCalendar(calendar: Calendar) {
        viewModelScope.launch {
            _calendar.emit(calendar)
            fetchCheckPointList(calendar.id)
            fetchScheduleList(calendar.id)
        }
    }

    fun fetchCalendarList() {
        viewModelScope.launch {
            _calendarList.emit(calendarLocalDataSource.fetchAllCalendar())
        }
    }

    private fun fetchCheckPointList(calendarId: Long) {
        viewModelScope.launch {
            _checkPointList.emit(checkPointLocalDataSource.fetchCalendarCheckPoints(calendarId))
        }
    }

    private fun fetchScheduleList(calendarId: Long) {
        viewModelScope.launch {
            scheduleLocalDataSource.fetchCalendarSchedules(calendarId)
                .map { schedule -> schedule.mapToCalendarScheduleObject() }
                .let { _scheduleList.emit(it) }
        }
    }

    fun setMenuItemOrder(order: Int) {
        viewModelScope.launch {
            _menuItemOrder.emit(order)
        }
    }

    private fun Schedule.mapToCalendarScheduleObject() = CalendarScheduleObject(
        id = id.toInt(),
        color = color,
        text = name,
        startDate = startDate,
        endDate = endDate
    )

    fun emitFabClickEvent() {
        viewModelScope.launch {
            _fabClickEvent.emit(calendar.value?.id ?: 0)
        }
    }

    fun emitDaySecondClickEvent(date: LocalDate) {
        viewModelScope.launch {
            _daySecondClickEvent.emit(date)
        }
    }

    fun fetchCalendarDesignObject() = calendarThemeDataSource.fetchCalendarTheme()
        .map { it.toCalendarDesignObject() }
}
