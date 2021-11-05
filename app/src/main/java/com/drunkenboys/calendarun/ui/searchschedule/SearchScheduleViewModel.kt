package com.drunkenboys.calendarun.ui.searchschedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.ui.searchschedule.model.DateItem
import com.drunkenboys.calendarun.ui.searchschedule.model.DateScheduleItem
import com.drunkenboys.calendarun.util.SingleLiveEvent
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

    private val _scheduleClickEvent = SingleLiveEvent<Int>()
    val scheduleClickEvent: LiveData<Int> = _scheduleClickEvent

    private val _isSearching = MutableLiveData(false)
    val isSearching: LiveData<Boolean> = _isSearching

    private var debounceJob: Job = Job()

    fun fetchScheduleList() {
        debounceJob.cancel()
        debounceJob = Job()
        _isSearching.value = true

        viewModelScope.launch(debounceJob) {
            delay(500)
            val today = Date()

            _listItem.value = scheduleDataSource.fetchAllSchedule()
                .filter { schedule -> schedule.startDate >= today }
                .mapToDateItem()

            _isSearching.value = false
        }
    }

    private fun List<Schedule>.mapToDateItem() = groupBy { schedule -> schedule.dayMillis() }
        .map { (dayMillis, scheduleList) ->
            val dateScheduleList = scheduleList.map { schedule ->
                DateScheduleItem(schedule, _scheduleClickEvent::setValue)
            }
            DateItem(Date(dayMillis), dateScheduleList)
        }
        .sortedBy { dateItem -> dateItem.date }

    private fun Schedule.dayMillis() = Calendar.getInstance().apply {
        time = this@dayMillis.startDate
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }.timeInMillis

    fun searchSchedule(word: String) {
        if (word.isEmpty()) {
            fetchScheduleList()
            return
        }
        debounceJob.cancel()
        debounceJob = Job()
        _isSearching.value = true

        viewModelScope.launch(debounceJob) {
            delay(500)
            _listItem.value = scheduleDataSource.fetchAllSchedule()
                .filter { schedule -> word in schedule.name }
                .mapToDateItem()

            _isSearching.value = false
        }
    }
}
