package com.drunkenboys.calendarun.ui.searchschedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.ui.searchschedule.model.DateItem
import com.drunkenboys.calendarun.ui.searchschedule.model.DateScheduleItem
import com.drunkenboys.calendarun.ui.searchschedule.model.ScheduleItem
import com.drunkenboys.calendarun.util.seconds
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
class SearchScheduleViewModel @Inject constructor(
    private val scheduleDataSource: ScheduleLocalDataSource
) : ViewModel() {

    val word = MutableStateFlow("")

    private val scheduleList = MutableStateFlow<List<ScheduleItem>>(emptyList())

    private val _listItem = MutableStateFlow<List<DateScheduleItem>>(emptyList())
    val listItem: StateFlow<List<DateScheduleItem>> = _listItem

    private val _scheduleClickEvent = MutableSharedFlow<Schedule>()
    val scheduleClickEvent: SharedFlow<Schedule> = _scheduleClickEvent

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private var debounceJob: Job = Job()

    init {
        fetchScheduleList()
    }

    fun fetchScheduleList() {
        viewModelScope.launch {
            _isSearching.value = true

            val today = LocalDate.now()

            _listItem.value = scheduleDataSource.fetchScheduleAfter(today.seconds)
                .map { schedule -> ScheduleItem(schedule) { emitScheduleClickEvent(schedule) } }
                .groupBy { scheduleItem -> scheduleItem.schedule.startDate.toLocalDate() }
                .flatMap { (localDate, scheduleList) -> listOf(DateItem(localDate)) + scheduleList }

            _isSearching.value = false
        }
    }

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
//            _isSearching.emit(true)
//
//
//            _isSearching.emit(false)
        }
    }

    companion object {

        private const val DEBOUNCE_DURATION = 500L
    }
}
