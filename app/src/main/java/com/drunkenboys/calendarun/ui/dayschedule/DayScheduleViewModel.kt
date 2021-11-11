package com.drunkenboys.calendarun.ui.dayschedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.idstore.IdStore
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.di.CalendarId
import com.drunkenboys.calendarun.ui.searchschedule.model.DateScheduleItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class DayScheduleViewModel @Inject constructor(
    @CalendarId private val calendarId: Long,
    private val scheduleDataSource: ScheduleLocalDataSource
) : ViewModel() {

    private val _dateString = MutableStateFlow("")
    val dateString: StateFlow<String> = _dateString

    private val _listItem = MutableStateFlow<List<DateScheduleItem>>(emptyList())
    val listItem: StateFlow<List<DateScheduleItem>> = _listItem

    private val _scheduleClickEvent = MutableSharedFlow<Unit>()
    val scheduleClickEvent: SharedFlow<Unit> = _scheduleClickEvent

    fun fetchScheduleList(localDate: LocalDate) {
        viewModelScope.launch {
            _dateString.emit(localDate.format(DateTimeFormatter.ofPattern("M월 d일")))

            scheduleDataSource.fetchCalendarSchedules(calendarId)
                .filter { localDate in it.startDate.toLocalDate()..it.endDate.toLocalDate() }
                .map { schedule ->
                    DateScheduleItem(schedule) { emitScheduleClickEvent(schedule) }
                }
                .sortedBy { dateScheduleItem -> dateScheduleItem.schedule.startDate }
                .let { _listItem.emit(it) }
        }
    }

    private fun emitScheduleClickEvent(schedule: Schedule) {
        viewModelScope.launch {
            IdStore.putId(IdStore.KEY_CALENDAR_ID, schedule.calendarId)
            IdStore.putId(IdStore.KEY_SCHEDULE_ID, schedule.id)
            _scheduleClickEvent.emit(Unit)
        }
    }
}
