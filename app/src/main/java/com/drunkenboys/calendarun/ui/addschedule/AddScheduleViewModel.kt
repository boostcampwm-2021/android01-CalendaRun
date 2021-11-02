package com.drunkenboys.calendarun.ui.addschedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.ui.addschedule.model.ScheduleNotificationType
import com.drunkenboys.calendarun.util.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddScheduleViewModel @Inject constructor(private val scheduleDataSource: ScheduleLocalDataSource) : ViewModel() {

    private var calendarId: Int = -1

    val title = MutableLiveData("")

    private val _startDate = MutableLiveData(Date())
    val startDate: LiveData<Date> = _startDate

    private val _endDate = MutableLiveData(Date())
    val endDate: LiveData<Date> = _endDate

    val memo = MutableLiveData("")

    private val _calendarName = MutableLiveData("test")
    val calendarName: LiveData<String> = _calendarName

    private val _notification = MutableLiveData(ScheduleNotificationType.TEN_MINUTES_AGO)
    val notification: MutableLiveData<ScheduleNotificationType> = _notification

    private val _tagColor = MutableLiveData<Int>()
    val tagColor: LiveData<Int> = _tagColor

    fun addSchedule() {
        if (!validateInput()) return

        viewModelScope.launch {
            val schedule = createScheduleInstance()

            scheduleDataSource.insertSchedule(schedule)
        }
    }

    private fun validateInput(): Boolean {
        if (calendarId < 0) return false
        if (title.value.isNullOrEmpty()) return false
        startDate.value ?: return false
        endDate.value ?: return false
        if (memo.value.isNullOrEmpty()) return false
        if (calendarName.value.isNullOrEmpty()) return false

        return true
    }

    private fun createScheduleInstance() = Schedule(
        id = 0,
        calendarId = calendarId,
        name = title.getOrThrow(),
        startDate = startDate.getOrThrow(),
        endDate = endDate.getOrThrow(),
        notification = getNotificationDate(),
        memo = memo.getOrThrow(),
        color = tagColor.getOrThrow()
    )

    private fun getNotificationDate(): Date? {
        val calendar = Calendar.getInstance()
        calendar.time = startDate.getOrThrow()

        when (notification.value) {
            ScheduleNotificationType.NONE -> return null
            ScheduleNotificationType.TEN_MINUTES_AGO -> calendar.add(Calendar.MINUTE, -10)
            ScheduleNotificationType.A_HOUR_AGO -> calendar.add(Calendar.HOUR_OF_DAY, -1)
            ScheduleNotificationType.A_DAY_AGO -> calendar.add(Calendar.DAY_OF_YEAR, -1)
        }

        return calendar.time
    }
}
