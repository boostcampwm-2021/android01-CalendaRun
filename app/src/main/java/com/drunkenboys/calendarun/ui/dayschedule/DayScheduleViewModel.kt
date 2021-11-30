package com.drunkenboys.calendarun.ui.dayschedule

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.KEY_CALENDAR_ID
import com.drunkenboys.calendarun.KEY_LOCAL_DATE
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.ui.searchschedule.model.ScheduleItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class DayScheduleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    scheduleDataSource: ScheduleLocalDataSource
) : ViewModel() {

    private val calendarId = savedStateHandle[KEY_CALENDAR_ID] ?: 0L
    private val localDate = savedStateHandle.get<String>(KEY_LOCAL_DATE)
        ?.let { LocalDate.parse(it) }

    val dateString = localDate?.format(DateTimeFormatter.ofPattern("M월 d일"))

    val listItem = scheduleDataSource.fetchCalendarSchedules(calendarId)
        .map { scheduleList ->
            scheduleList.filter { localDate in it.startDate.toLocalDate()..it.endDate.toLocalDate() }
                .map { schedule -> ScheduleItem(schedule) { emitScheduleClickEvent(schedule) } }
                .sortedBy { dateScheduleItem -> dateScheduleItem.schedule.startDate }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _scheduleClickEvent = MutableSharedFlow<Schedule>()
    val scheduleClickEvent: SharedFlow<Schedule> = _scheduleClickEvent

    private fun emitScheduleClickEvent(schedule: Schedule) {
        viewModelScope.launch {
            _scheduleClickEvent.emit(schedule)
        }
    }
}
