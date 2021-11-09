package com.drunkenboys.calendarun.ui.searchschedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.idstore.IdStore
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.ui.searchschedule.model.DateItem
import com.drunkenboys.calendarun.ui.searchschedule.model.DateScheduleItem
import com.drunkenboys.calendarun.util.SingleLiveEvent
import com.drunkenboys.calendarun.util.extensions.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SearchScheduleViewModel @Inject constructor(
    private val scheduleDataSource: ScheduleLocalDataSource
) : ViewModel() {

    val word = MutableLiveData("")

    private val _listItem = MutableLiveData<List<DateItem>>()
    val listItem: LiveData<List<DateItem>> = _listItem

    private val _scheduleClickEvent = SingleLiveEvent<Unit>()
    val scheduleClickEvent: LiveData<Unit> = _scheduleClickEvent

    private val _isSearching = MutableLiveData(false)
    val isSearching: LiveData<Boolean> = _isSearching

    private var debounceJob: Job = Job()

    fun fetchScheduleList() {
        if (word.value.isNullOrEmpty()) {
            _isSearching.value = true

            viewModelScope.launch {
                val today = Date()

                _listItem.value = scheduleDataSource.fetchAllSchedule()
                    .filter { schedule -> schedule.startDate >= today }
                    .mapToDateItem()

                _isSearching.value = false
            }
        } else {
            searchSchedule(word.getOrThrow())
        }
    }

    private fun List<Schedule>.mapToDateItem() = groupBy { schedule -> schedule.dayMillis() }
        .map { (dayMillis, scheduleList) ->
            val dateScheduleList = scheduleList.map { schedule ->
                DateScheduleItem(schedule) { emitScheduleClickEvent(schedule) }
            }
            DateItem(Date(dayMillis), dateScheduleList)
        }
        .sortedBy { dateItem -> dateItem.date }

    private fun emitScheduleClickEvent(schedule: Schedule) {
        IdStore.putId(IdStore.KEY_CALENDAR_ID, schedule.calendarId)
        IdStore.putId(IdStore.KEY_SCHEDULE_ID, schedule.id)
        _scheduleClickEvent.value = Unit
    }

    private fun Schedule.dayMillis() = Calendar.getInstance().apply {
        time = this@dayMillis.startDate
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    fun searchSchedule(word: String) {
        debounceJob.cancel()
        debounceJob = Job()
        if (word.isEmpty()) {
            fetchScheduleList()
            return
        }
        _isSearching.value = true

        viewModelScope.launch(debounceJob) {
            delay(DEBOUNCE_DURATION)
            _listItem.value = scheduleDataSource.fetchAllSchedule()
                .filter { schedule -> word in schedule.name }
                .mapToDateItem()

            _isSearching.value = false
        }
    }

    companion object {

        private const val DEBOUNCE_DURATION = 500L
    }
}
