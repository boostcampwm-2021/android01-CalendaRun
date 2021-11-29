package com.drunkenboys.calendarun.ui.searchschedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleDao
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.ui.searchschedule.model.DateItem
import com.drunkenboys.calendarun.ui.searchschedule.model.DateScheduleItem
import com.drunkenboys.calendarun.ui.searchschedule.model.ScheduleItem
import com.drunkenboys.calendarun.util.defaultZoneOffset
import com.drunkenboys.calendarun.util.extensions.throttleFirst
import com.drunkenboys.calendarun.util.seconds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class SearchScheduleViewModel @Inject constructor(
    private val scheduleDataSource: ScheduleLocalDataSource
) : ViewModel() {

    val word = MutableStateFlow("")

    private var scheduleList: List<Schedule> = emptyList()

    private val _listItem = MutableStateFlow<List<DateScheduleItem>>(emptyList())
    val listItem: StateFlow<List<DateScheduleItem>> = _listItem

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _scheduleClickEvent = MutableSharedFlow<Schedule>()
    val scheduleClickEvent: SharedFlow<Schedule> = _scheduleClickEvent

    private val searchPrevEvent = MutableSharedFlow<Unit>()
    private val searchNextEvent = MutableSharedFlow<Unit>()

    private var debounceJob: Job = Job()

    private var prevKey: LocalDateTime?
    private var nextKey: LocalDateTime?

    init {
        val today = LocalDateTime.now()
        prevKey = today
        nextKey = today
        collectSearchEvent()
        searchSchedule()
    }

    fun trySearchPrev() {
        viewModelScope.launch {
            searchPrevEvent.emit(Unit)
        }
    }

    fun trySearchNext() {
        viewModelScope.launch {
            searchNextEvent.emit(Unit)
        }
    }

    private fun collectSearchEvent() {
        viewModelScope.launch {
            launch {
                searchPrevEvent.throttleFirst(PAGING_THROTTLE_DURATION)
                    .collect { prevKey?.let { searchPrev(it) } }
            }
            launch {
                searchNextEvent.throttleFirst(PAGING_THROTTLE_DURATION)
                    .collect { nextKey?.let { searchNext(it) } }
            }
        }
    }

    fun searchSchedule(word: String = this.word.value) {
        debounceJob.cancel()
        debounceJob = viewModelScope.launch {
            _isSearching.value = true
            delay(DEBOUNCE_DURATION)

            val today = LocalDate.now()

            scheduleList = scheduleDataSource.fetchMatchedScheduleAfter(word, today.seconds - 1)
            if (scheduleList.isEmpty()) scheduleList = scheduleDataSource.fetchMatchedScheduleBefore(word, today.seconds)
            prevKey = scheduleList.firstOrNull()?.startDate
            nextKey = scheduleList.lastOrNull()?.startDate
            updateListItem()

            _isSearching.value = false
        }
    }

    private suspend fun searchPrev(key: LocalDateTime) {
        val newList = scheduleDataSource.fetchMatchedScheduleBefore(word.value, key.toEpochSecond(defaultZoneOffset))
        if (newList.isEmpty()) {
            prevKey = null
        } else {
            scheduleList = newList + scheduleList.take(ScheduleDao.SCHEDULE_PAGING_SIZE)
            prevKey = scheduleList.firstOrNull()?.startDate
            nextKey = scheduleList.lastOrNull()?.startDate

            updateListItem()
        }
    }

    private suspend fun searchNext(key: LocalDateTime) {
        val newList = scheduleDataSource.fetchMatchedScheduleAfter(word.value, key.toEpochSecond(defaultZoneOffset))
        if (newList.isEmpty()) {
            nextKey = null
        } else {
            scheduleList = scheduleList.takeLast(ScheduleDao.SCHEDULE_PAGING_SIZE) + newList
            prevKey = scheduleList.firstOrNull()?.startDate
            nextKey = scheduleList.lastOrNull()?.startDate

            updateListItem()
        }
    }

    private fun updateListItem() {
        _listItem.value = scheduleList.map { schedule -> ScheduleItem(schedule) { emitScheduleClickEvent(schedule) } }
            .groupBy { scheduleItem -> scheduleItem.schedule.startDate.toLocalDate() }
            .flatMap { (localDate, scheduleList) -> listOf(DateItem(localDate)) + scheduleList }
    }

    private fun emitScheduleClickEvent(schedule: Schedule) {
        viewModelScope.launch {
            _scheduleClickEvent.emit(schedule)
        }
    }

    fun deleteSchedule(id: Long) {
        scheduleList = scheduleList.filter { it.id != id }
        updateListItem()
    }

    companion object {

        private const val DEBOUNCE_DURATION = 500L
        private const val PAGING_THROTTLE_DURATION = 600L
    }
}
