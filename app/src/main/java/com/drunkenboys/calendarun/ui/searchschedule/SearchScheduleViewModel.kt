package com.drunkenboys.calendarun.ui.searchschedule

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.ui.searchschedule.model.DateItem
import com.drunkenboys.calendarun.ui.searchschedule.model.DateScheduleItem
import com.drunkenboys.calendarun.ui.searchschedule.model.ScheduleItem
import com.drunkenboys.calendarun.util.extensions.throttleFirst
import com.drunkenboys.calendarun.util.seconds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
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

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _scheduleClickEvent = MutableSharedFlow<Schedule>()
    val scheduleClickEvent: SharedFlow<Schedule> = _scheduleClickEvent

    private val searchPrevEvent = MutableSharedFlow<Unit>()
    private val searchNextEvent = MutableSharedFlow<Unit>()

    private var debounceJob: Job = Job()

    init {
        collectSearchEvent()
        searchSchedule()
    }

    fun tryFetchPrev() {
        viewModelScope.launch {
            Log.d("SearchScheduleViewModel", "tryFetchPrev: ")
            searchPrevEvent.emit(Unit)
        }
    }

    fun tryFetchNext() {
        viewModelScope.launch {
            Log.d("SearchScheduleViewModel", "tryFetchNext: ")
            searchNextEvent.emit(Unit)
        }
    }

    private fun collectSearchEvent() {
        viewModelScope.launch {
            launch {
                searchPrevEvent.throttleFirst(600)
                    .collect { Log.d("SearchScheduleViewModel", "collectSearchEvent prev") }
            }
            launch {
                searchNextEvent.throttleFirst(600)
                    .collect { Log.d("SearchScheduleViewModel", "collectSearchEvent next") }
            }
        }
    }

    fun searchSchedule(word: String = "") {
        debounceJob.cancel()
        debounceJob = viewModelScope.launch {
            _isSearching.value = true
            delay(DEBOUNCE_DURATION)

            val today = LocalDate.now()

            _listItem.value = scheduleDataSource.fetchMatchedScheduleAfter(word, today.seconds)
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

    companion object {

        private const val DEBOUNCE_DURATION = 500L
    }
}
