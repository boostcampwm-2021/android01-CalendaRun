package com.drunkenboys.calendarun.ui.dayschedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.idstore.IdStore
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.di.CalendarId
import com.drunkenboys.calendarun.ui.searchschedule.model.DateItem
import com.drunkenboys.calendarun.ui.searchschedule.model.DateScheduleItem
import com.drunkenboys.calendarun.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DayScheduleViewModel @Inject constructor(
    @CalendarId private val calendarId: Long,
    private val scheduleDataSource: ScheduleLocalDataSource
) : ViewModel() {

    private val _listItem = MutableLiveData<List<DateItem>>()
    val listItem: LiveData<List<DateItem>> = _listItem

    private val _scheduleClickEvent = SingleLiveEvent<Unit>()
    val scheduleClickEvent: LiveData<Unit> = _scheduleClickEvent

    fun fetchScheduleList(localDate: LocalDate) {
        viewModelScope.launch {
            _listItem.value = scheduleDataSource.fetchCalendarSchedules(calendarId)
                .filter { it.startDate.toLocalDate() == localDate }
                .mapToDateItem()
        }
    }

    private fun List<Schedule>.mapToDateItem() = groupBy { schedule -> schedule.startDate.toLocalDate() }
        .map { (localDate, scheduleList) ->
            val dateScheduleList = scheduleList.map { schedule ->
                DateScheduleItem(schedule) { emitScheduleClickEvent(schedule) }
            }
            DateItem(localDate, dateScheduleList)
        }
        .sortedBy { dateItem -> dateItem.date }

    private fun emitScheduleClickEvent(schedule: Schedule) {
        IdStore.putId(IdStore.KEY_CALENDAR_ID, schedule.calendarId)
        IdStore.putId(IdStore.KEY_SCHEDULE_ID, schedule.id)
        _scheduleClickEvent.value = Unit
    }
}
