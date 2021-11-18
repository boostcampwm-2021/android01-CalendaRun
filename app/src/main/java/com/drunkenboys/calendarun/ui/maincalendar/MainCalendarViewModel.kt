package com.drunkenboys.calendarun.ui.maincalendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import com.drunkenboys.calendarun.data.checkpoint.entity.CheckPoint
import com.drunkenboys.calendarun.data.checkpoint.local.CheckPointLocalDataSource
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.util.nextDay
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import com.drunkenboys.ckscalendar.data.CalendarSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainCalendarViewModel @Inject constructor(
    private val calendarLocalDataSource: CalendarLocalDataSource,
    private val checkPointLocalDataSource: CheckPointLocalDataSource,
    private val scheduleLocalDataSource: ScheduleLocalDataSource
) : ViewModel() {

    private val _calendar = MutableStateFlow<Calendar?>(null)
    val calendar: StateFlow<Calendar?> = _calendar

    private val _calendarList = MutableStateFlow<List<Calendar>>(emptyList())
    val calendarList: StateFlow<List<Calendar>> = _calendarList

    private val _checkPointList = MutableStateFlow<List<CheckPoint>>(emptyList())

    private val _scheduleList = MutableStateFlow<List<CalendarScheduleObject>>(emptyList())
    val scheduleList: StateFlow<List<CalendarScheduleObject>> = _scheduleList

    private val _calendarSetList = MutableStateFlow<List<CalendarSet>>(emptyList())
    val calendarSetList: StateFlow<List<CalendarSet>> = _calendarSetList

    private val _menuItemOrder = MutableStateFlow(0)
    val menuItemOrder: StateFlow<Int> = _menuItemOrder

    private val _fabClickEvent = MutableSharedFlow<Long>()
    val fabClickEvent: SharedFlow<Long> = _fabClickEvent

    private val _daySecondClickEvent = MutableSharedFlow<LocalDate>()
    val daySecondClickEvent: SharedFlow<LocalDate> = _daySecondClickEvent

    fun setCalendar(calendar: Calendar) {
        viewModelScope.launch {
            _calendar.emit(calendar)
            fetchCheckPointList(calendar.id).join()
            fetchScheduleList(calendar.id)
            createCalendarSetList()
        }
    }

    fun fetchCalendarList() {
        viewModelScope.launch {
            _calendarList.emit(calendarLocalDataSource.fetchAllCalendar())
        }
    }

    private fun fetchCheckPointList(calendarId: Long): Job {
        return viewModelScope.launch {
            _checkPointList.emit(checkPointLocalDataSource.fetchCalendarCheckPoints(calendarId).sortedBy { checkPoint -> checkPoint.date })
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

    private fun createCalendarSetList() {
        viewModelScope.launch {
            val calendarNameList = createCalendarSetNameList() ?: return@launch
            val calendarDateList = createCalendarSetDateList() ?: return@launch
            val (calendarStartDateList, calendarEndDateList) = calendarDateList.first to calendarDateList.second

            val calendarSetList = mutableListOf<CalendarSet>()

            for (i in calendarNameList.indices) {
                calendarSetList.add(
                    CalendarSet(
                        id = i,
                        name = calendarNameList[i],
                        startDate = calendarStartDateList[i],
                        endDate = calendarEndDateList[i]
                    )
                )
            }

            _calendarSetList.emit(calendarSetList.toList())
        }
    }

    private fun createCalendarSetNameList(): List<String>? {
        val calendarName = _calendar.value?.name ?: return null
        val checkPointNameList = _checkPointList.value.map { checkPoint -> checkPoint.name }

        val calendarSetNameList = mutableListOf<String>()
        checkPointNameList.forEach { checkPointName -> calendarSetNameList.add(checkPointName) }
        calendarSetNameList.add(calendarName)

        return calendarSetNameList
    }

    private fun createCalendarSetDateList(): Pair<List<LocalDate>, List<LocalDate>>? {
        val startDate = _calendar.value?.startDate ?: return null
        val endDate = _calendar.value?.endDate ?: return null
        val checkPointDateList = _checkPointList.value.map { checkPoint -> checkPoint.date }
        val calendarStartDateList = mutableListOf<LocalDate>()
        val calendarEndDateList = mutableListOf<LocalDate>()

        calendarStartDateList.add(startDate)
        if (checkPointDateList.isNotEmpty()) {
            calendarStartDateList.addAll(checkPointDateList.map { localDate -> localDate.nextDay() })
            calendarEndDateList.addAll(checkPointDateList)
        }
        calendarEndDateList.add(endDate)

        return calendarStartDateList to calendarEndDateList
    }
}
