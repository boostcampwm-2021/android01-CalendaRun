package com.drunkenboys.calendarun.ui.maincalendar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.KEY_CALENDAR_ID
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
@ExperimentalCoroutinesApi
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

    val calendarSetList = calendarId.flatMapLatest { calendarId ->
        checkPointLocalDataSource.fetchCalendarCheckPoints(calendarId)
            .map { checkPointList ->
                checkPointList.map { checkPoint -> checkPoint.toCalendarSet() }
            }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val scheduleList = calendarId.flatMapLatest { calendarId ->
        scheduleLocalDataSource.fetchCalendarSchedules(calendarId)
            .map { scheduleList ->
                scheduleList.map { schedule -> schedule.toCalendarScheduleObject() }
            }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val calendarDesignObject = calendarThemeDataSource.fetchCalendarTheme()
        .map { it.toCalendarDesignObject() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _isMonthCalendar = MutableStateFlow(true)
    val isMonthCalendar: StateFlow<Boolean> = _isMonthCalendar

    private val _fabClickEvent = MutableSharedFlow<Long>()
    val fabClickEvent: SharedFlow<Long> = _fabClickEvent

    private val _daySecondClickEvent = MutableSharedFlow<LocalDate>()
    val daySecondClickEvent: SharedFlow<LocalDate> = _daySecondClickEvent

    private val _licenseClickEvent = MutableSharedFlow<Unit>()
    val licenseClickEvent: SharedFlow<Unit> = _licenseClickEvent

    fun setCalendarId(calendarId: Long) {
        savedStateHandle.set(KEY_CALENDAR_ID, calendarId)
    }

    fun toggleCalendar() {
        _isMonthCalendar.value = !_isMonthCalendar.value
    }

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

    fun emitLicenseClickEvent() {
        viewModelScope.launch {
            _licenseClickEvent.emit(Unit)
        }
    }

    private fun CheckPoint.toCalendarSet() = CalendarSet(
        id = id.toInt(),
        name = name,
        startDate = startDate,
        endDate = endDate
    )

    private fun Schedule.toCalendarScheduleObject() = CalendarScheduleObject(
        id = id.toInt(),
        color = color,
        text = name,
        startDate = startDate,
        endDate = endDate
    )
}
