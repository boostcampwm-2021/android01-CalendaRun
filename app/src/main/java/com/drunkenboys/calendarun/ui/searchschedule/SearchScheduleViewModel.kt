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
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SearchScheduleViewModel @Inject constructor(
    private val scheduleDataSource: ScheduleLocalDataSource
) : ViewModel() {

    val word = MutableLiveData<String>()

    private val _listItem = MutableLiveData<List<DateItem>>()
    val listItem: LiveData<List<DateItem>> = _listItem

    private val _scheduleClickEvent = SingleLiveEvent<Int>()
    val scheduleClickEvent: LiveData<Int> = _scheduleClickEvent

    fun fetchScheduleList() {
        viewModelScope.launch {
            val today = Date()

            _listItem.value = scheduleDataSource.fetchAllSchedule()
                .filter { schedule -> schedule.startDate >= today }
                .groupBy { schedule -> schedule.dayMillis() }
                .map { (dayMillis, scheduleList) ->
                    val dateScheduleList = scheduleList.map { schedule ->
                        DateScheduleItem(schedule, _scheduleClickEvent::setValue)
                    }
                    DateItem(Date(dayMillis), dateScheduleList)
                }
                .sortedBy { dateItem -> dateItem.date }
        }
    }

    private fun Schedule.dayMillis() = Calendar.getInstance().apply {
        time = this@dayMillis.startDate
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }.timeInMillis
}
