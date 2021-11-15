package com.drunkenboys.calendarun.ui.searchschedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.ui.searchschedule.model.DateItem
import com.drunkenboys.calendarun.ui.searchschedule.model.DateScheduleItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class SearchScheduleViewModel @Inject constructor(
    private val scheduleDataSource: ScheduleLocalDataSource
) : ViewModel() {

    val word = MutableStateFlow("")

    private val _listItem = MutableStateFlow<List<DateItem>>(emptyList())
    val listItem: StateFlow<List<DateItem>> = _listItem

    private val _scheduleClickEvent = MutableSharedFlow<Schedule>()
    val scheduleClickEvent: SharedFlow<Schedule> = _scheduleClickEvent

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private var debounceJob: Job = Job()

    fun fetchScheduleList() {
        viewModelScope.launch {
            if (word.value.isEmpty()) {
                _isSearching.emit(true)

                val today = LocalDateTime.now()

                scheduleDataSource.fetchAllSchedule()
                    .filter { schedule -> schedule.startDate >= today }
                    .mapToDateItem()
                    .let { _listItem.emit(it) }

                _isSearching.emit(false)
            } else {
                searchSchedule(word.value)
            }
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
        viewModelScope.launch {
            _scheduleClickEvent.emit(schedule)
        }
    }

    fun searchSchedule(word: String) {
        debounceJob.cancel()
        debounceJob = Job()
        if (word.isEmpty()) {
            fetchScheduleList()
            return
        }

        viewModelScope.launch(debounceJob) {
            _isSearching.emit(true)

            delay(DEBOUNCE_DURATION)
            scheduleDataSource.fetchAllSchedule()
                .filter { schedule -> word in schedule.name }
                .mapToDateItem()
                .let { _listItem.emit(it) }

            _isSearching.emit(false)
        }
    }

    companion object {

        private const val DEBOUNCE_DURATION = 500L
    }
}
