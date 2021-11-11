package com.drunkenboys.calendarun.ui.dayschedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.idstore.IdStore
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.di.CalendarId
import com.drunkenboys.calendarun.ui.searchschedule.model.DateScheduleItem
import com.drunkenboys.calendarun.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class DayScheduleViewModel @Inject constructor(
    @CalendarId private val calendarId: Long,
    private val scheduleDataSource: ScheduleLocalDataSource
) : ViewModel() {

    private val _dateString = MutableLiveData<String>()
    val dateString: LiveData<String> = _dateString

    private val _listItem = MutableLiveData<List<DateScheduleItem>>()
    val listItem: LiveData<List<DateScheduleItem>> = _listItem

    private val _scheduleClickEvent = SingleLiveEvent<Unit>()
    val scheduleClickEvent: LiveData<Unit> = _scheduleClickEvent

    fun fetchScheduleList(localDate: LocalDate) {
        _dateString.value = localDate.format(DateTimeFormatter.ofPattern("M월 d일"))

        viewModelScope.launch {
            _listItem.value = scheduleDataSource.fetchCalendarSchedules(calendarId)
                .filter { localDate in it.startDate.toLocalDate()..it.endDate.toLocalDate() }
                .map { schedule ->
                    DateScheduleItem(schedule) { emitScheduleClickEvent(schedule) }
                }
                .sortedBy { dateScheduleItem -> dateScheduleItem.schedule.startDate }
        }
    }

    private fun emitScheduleClickEvent(schedule: Schedule) {
        IdStore.putId(IdStore.KEY_CALENDAR_ID, schedule.calendarId)
        IdStore.putId(IdStore.KEY_SCHEDULE_ID, schedule.id)
        _scheduleClickEvent.value = Unit
    }
}
