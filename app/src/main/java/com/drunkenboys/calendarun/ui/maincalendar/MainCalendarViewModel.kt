package com.drunkenboys.calendarun.ui.maincalendar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
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
    private val savedStateHandle: SavedStateHandle,
    private val calendarLocalDataSource: CalendarLocalDataSource,
    private val checkPointLocalDataSource: CheckPointLocalDataSource,
    private val scheduleLocalDataSource: ScheduleLocalDataSource,
    calendarThemeDataSource: CalendarThemeLocalDataSource
) : ViewModel() {

    val calendarId = savedStateHandle.getLiveData<Long>(KEY_CALENDAR_ID)
        .asFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, 1L)

    val calendarName = calendarId.map {
        calendarLocalDataSource.fetchCalendar(it).name
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val calendarList = calendarLocalDataSource.fetchAllCalendar()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _scheduleList = MutableStateFlow<List<CalendarScheduleObject>>(emptyList())
    val scheduleList: StateFlow<List<CalendarScheduleObject>> = _scheduleList

    private val _calendarSetList = MutableStateFlow<List<CalendarSet>>(emptyList())
    val calendarSetList: StateFlow<List<CalendarSet>> = _calendarSetList

    private val _selectedCalendarIndex = MutableStateFlow(0)
    val selectCalendarIndex: StateFlow<Int> = _selectedCalendarIndex

    private val _isMonthCalendar = MutableStateFlow(true)
    val isMonthCalendar: StateFlow<Boolean> = _isMonthCalendar

    val calendarDesignObject = calendarThemeDataSource.fetchCalendarTheme()
        .map { it.toCalendarDesignObject() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _fabClickEvent = MutableSharedFlow<Long>()
    val fabClickEvent: SharedFlow<Long> = _fabClickEvent

    private val _daySecondClickEvent = MutableSharedFlow<LocalDate>()
    val daySecondClickEvent: SharedFlow<LocalDate> = _daySecondClickEvent

    private val _licenseClickEvent = MutableSharedFlow<Unit>()
    val licenseClickEvent: SharedFlow<Unit> = _licenseClickEvent

    init {
        fetchCalendar()
    }

    fun fetchCalendar() {
        viewModelScope.launch {
            val currentCalendar = calendarLocalDataSource.fetchCalendar(calendarId.value)
            setCalendar(currentCalendar)
        }
    }

    fun setCalendar(calendar: Calendar) {
        viewModelScope.launch {
            createCalendarSetList(calendar.id, fetchCheckPointList(calendar.id))
            fetchScheduleList(calendar.id)
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
            _fabClickEvent.emit(calendarId.value)
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

    fun emitLicenseClickEvent() {
        viewModelScope.launch {
            _licenseClickEvent.emit(Unit)
        }
    }

    fun setCalendarId(calendarId: Long) {
        savedStateHandle.set(KEY_CALENDAR_ID, calendarId)
    }

    fun toggleCalendar() {
        _isMonthCalendar.value = !_isMonthCalendar.value
    }
}
