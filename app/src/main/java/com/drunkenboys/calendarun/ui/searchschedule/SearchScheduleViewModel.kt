package com.drunkenboys.calendarun.ui.searchschedule

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

    private var prevKey: LocalDate?
    private var nextKey: LocalDate?

    init {
        val today = LocalDate.now()
        prevKey = today
        nextKey = today
        collectSearchEvent()
        searchSchedule()
    }

    fun tryFetchPrev() {
        viewModelScope.launch {
            searchPrevEvent.emit(Unit)
        }
    }

    fun tryFetchNext() {
        viewModelScope.launch {
            searchNextEvent.emit(Unit)
        }
    }

    private fun collectSearchEvent() {
        viewModelScope.launch {
            launch {
                searchPrevEvent.throttleFirst(600)
                    .collect { searchSchedule(action = SearchAction.PREV) }
            }
            launch {
                searchNextEvent.throttleFirst(600)
                    .collect { searchSchedule(action = SearchAction.NEXT) }
            }
        }
    }

    fun searchSchedule(word: String = "", action: SearchAction = SearchAction.NEXT) {
        debounceJob.cancel()
        debounceJob = viewModelScope.launch {
            _isSearching.value = true
            delay(DEBOUNCE_DURATION)

            if (action == SearchAction.PREV) {
                prevKey?.let { key ->
                    val newList = scheduleDataSource.fetchMatchedScheduleBefore(word, key.seconds)
                    if (newList.size == 1) {
                        prevKey = null
                        return@let
                    }
                    scheduleList = newList + scheduleList.take(30)
                    prevKey = scheduleList.firstOrNull()?.startDate?.toLocalDate()
                    nextKey = scheduleList.lastOrNull()?.startDate?.toLocalDate()
                }
            } else if (action == SearchAction.NEXT) {
                nextKey?.let { key ->
                    val newList = scheduleDataSource.fetchMatchedScheduleAfter(word, key.seconds)
                    if (newList.size == 1) {
                        nextKey = null
                        return@let
                    }
                    scheduleList = scheduleList.takeLast(30) + newList
                    prevKey = scheduleList.firstOrNull()?.startDate?.toLocalDate()
                    nextKey = scheduleList.lastOrNull()?.startDate?.toLocalDate()
                }
            }

            _listItem.value = scheduleList.map { schedule -> ScheduleItem(schedule) { emitScheduleClickEvent(schedule) } }
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

enum class SearchAction {
    PREV, NEXT
}
