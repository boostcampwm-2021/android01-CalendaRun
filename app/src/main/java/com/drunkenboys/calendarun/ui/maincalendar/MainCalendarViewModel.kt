package com.drunkenboys.calendarun.ui.maincalendar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.KEY_CALENDAR_ID
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import com.drunkenboys.calendarun.data.calendartheme.local.CalendarThemeLocalDataSource
import com.drunkenboys.calendarun.data.checkpoint.entity.CheckPoint
import com.drunkenboys.calendarun.data.checkpoint.local.CheckPointLocalDataSource
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.ui.theme.toCalendarDesignObject
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import com.drunkenboys.ckscalendar.data.CalendarSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainCalendarViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val calendarLocalDataSource: CalendarLocalDataSource,
    private val checkPointLocalDataSource: CheckPointLocalDataSource,
    private val scheduleLocalDataSource: ScheduleLocalDataSource,
    private val calendarThemeDataSource: CalendarThemeLocalDataSource
) : ViewModel() {

    private var calendarId = savedStateHandle[KEY_CALENDAR_ID] ?: 1L

    private val _calendar = MutableStateFlow<Calendar?>(null)
    val calendar: StateFlow<Calendar?> = _calendar

    private val _calendarList = MutableStateFlow<List<Calendar>>(emptyList())
    val calendarList: StateFlow<List<Calendar>> = _calendarList

    private val _scheduleList = MutableStateFlow<List<CalendarScheduleObject>>(emptyList())
    val scheduleList: StateFlow<List<CalendarScheduleObject>> = _scheduleList

    private val _calendarSetList = MutableStateFlow<List<CalendarSet>>(emptyList())
    val calendarSetList: StateFlow<List<CalendarSet>> = _calendarSetList

    private val _selectedCalendarIndex = MutableStateFlow(0)
    val selectCalendarIndex: StateFlow<Int> = _selectedCalendarIndex

    private val _fabClickEvent = MutableSharedFlow<Long>()
    val fabClickEvent: SharedFlow<Long> = _fabClickEvent

    private val _daySecondClickEvent = MutableSharedFlow<LocalDate>()
    val daySecondClickEvent: SharedFlow<LocalDate> = _daySecondClickEvent


    private val _licenseClickEvent = MutableSharedFlow<Unit>()
    val licenseClickEvent: SharedFlow<Unit> = _licenseClickEvent

    fun fetchCalendar() {
        viewModelScope.launch {
            var currentCalendar = calendarLocalDataSource.fetchCalendar(calendarId)
            if (currentCalendar == null) {
                calendarId = 1L
                _selectedCalendarIndex.emit(0)
                currentCalendar = calendarLocalDataSource.fetchCalendar(calendarId)
            }
            setCalendar(currentCalendar)
        }
    }

    fun setCalendar(calendar: Calendar) {
        viewModelScope.launch {
            _calendar.emit(calendar)
            calendarId = calendar.id
            createCalendarSetList(calendar.id, fetchCheckPointList(calendar.id))
            fetchScheduleList(calendar.id)
        }
    }

    fun fetchCalendarList() {
        viewModelScope.launch {
            _calendarList.emit(calendarLocalDataSource.fetchAllCalendar())
        }
    }

    private suspend fun fetchCheckPointList(calendarId: Long) = checkPointLocalDataSource.fetchCalendarCheckPoints(calendarId)

    private fun fetchScheduleList(calendarId: Long) {
        viewModelScope.launch {
            scheduleLocalDataSource.fetchCalendarSchedules(calendarId)
                .map { schedule -> schedule.mapToCalendarScheduleObject() }
                .let { _scheduleList.emit(it) }
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

    private fun createCalendarSetList(id: Long, checkPointList: List<CheckPoint>) {
        viewModelScope.launch {
            val calendarSetList = mutableListOf<CalendarSet>()

            checkPointList.forEach { checkPoint ->
                calendarSetList.add(
                    CalendarSet(
                        id = id.toInt(),
                        name = checkPoint.name,
                        startDate = checkPoint.startDate,
                        endDate = checkPoint.endDate
                    )
                )
            }

            _calendarSetList.emit(calendarSetList.toList())
        }
    }

    fun setSelectedCalendarIndex(index: Int) {
        viewModelScope.launch {
            _selectedCalendarIndex.emit(index)
        }
    }

    fun fetchCalendarDesignObject() = calendarThemeDataSource.fetchCalendarTheme()
        .map { it.toCalendarDesignObject() }

    fun emitLicenseClickEvent() {
        viewModelScope.launch {
            _licenseClickEvent.emit(Unit)
        }
    }
}
