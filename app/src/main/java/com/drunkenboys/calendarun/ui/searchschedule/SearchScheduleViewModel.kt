package com.drunkenboys.calendarun.ui.searchschedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.ui.searchschedule.model.DateItem
import com.drunkenboys.calendarun.ui.searchschedule.model.DateScheduleItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SearchScheduleViewModel @Inject constructor(
    private val scheduleDataSource: ScheduleLocalDataSource
) : ViewModel() {

    private val _listItem = MutableLiveData<List<DateItem>>()
    val listItem: LiveData<List<DateItem>> = _listItem

    fun fetchScheduleList() {
        viewModelScope.launch {
            val today = Date()

            _listItem.value = scheduleDataSource.fetchAllSchedule()
                .filter { schedule -> schedule.startDate >= today }
                .groupBy { schedule -> schedule.dayMillis() }
                .map { (dayMillis, scheduleList) -> DateItem(Date(dayMillis), scheduleList.map { DateScheduleItem(it) }) }
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
