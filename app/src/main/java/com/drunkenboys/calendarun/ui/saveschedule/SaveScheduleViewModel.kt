package com.drunkenboys.calendarun.ui.saveschedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.ui.saveschedule.model.BehaviorType
import com.drunkenboys.calendarun.util.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SaveScheduleViewModel @Inject constructor(private val scheduleDataSource: ScheduleLocalDataSource) : ViewModel() {

    private var scheduleId: Int? = null

    private var calendarId: Int? = null

    private lateinit var behaviorType: BehaviorType

    val title = MutableLiveData("")

    val startDate = MutableLiveData(Date())

    val endDate = MutableLiveData(Date())

    val memo = MutableLiveData("")

    private val _calendarName = MutableLiveData("test")
    val calendarName: LiveData<String> = _calendarName

    val notificationType = MutableLiveData(Schedule.NotificationType.TEN_MINUTES_AGO)

    private val _tagColor = MutableLiveData(R.color.black)
    val tagColor: LiveData<Int> = _tagColor

    private val _saveScheduleEvent = MutableLiveData<Unit>()
    val saveScheduleEvent: LiveData<Unit> = _saveScheduleEvent

    fun init(scheduleId: Int, calendarId: Int, calendarName: String, behaviorType: BehaviorType) {
        this.scheduleId = scheduleId
        this.calendarId = calendarId
        _calendarName.value = calendarName
        this.behaviorType = behaviorType

        if (behaviorType == BehaviorType.UPDATE) initData()
    }

    private fun initData() {
        val scheduleId = scheduleId ?: return
        if (scheduleId < 0) return

        viewModelScope.launch {
            val schedule = scheduleDataSource.fetchSchedule(scheduleId)

            title.value = schedule.name
            startDate.value = schedule.startDate
            endDate.value = schedule.endDate
            memo.value = schedule.memo
            notificationType.value = schedule.notificationType
            _tagColor.value = schedule.color
        }
    }

    fun Date.toFormatString(): String {
        val calendar = Calendar.getInstance()
        calendar.time = this

        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "오전" else "오후"
        val dateFormat = SimpleDateFormat("MM월 dd일 $amPm hh:mm", Locale.getDefault())

        return dateFormat.format(this)
    }

    fun saveSchedule() {
        if (isInvalidInput()) return

        viewModelScope.launch {
            val schedule = createScheduleInstance()

            when (behaviorType) {
                BehaviorType.INSERT -> scheduleDataSource.insertSchedule(schedule)
                BehaviorType.UPDATE -> scheduleDataSource.updateSchedule(schedule)
            }
            _saveScheduleEvent.value = Unit
        }
    }

    private fun isInvalidInput(): Boolean {
        scheduleId ?: return true
        calendarId ?: return true
        if (!this::behaviorType.isInitialized) return true
        if (title.value.isNullOrEmpty()) return true
        startDate.value ?: return true
        endDate.value ?: return true
        if (calendarName.value.isNullOrEmpty()) return true

        return false
    }

    private fun createScheduleInstance() = Schedule(
        id = scheduleId ?: throw IllegalArgumentException(),
        calendarId = calendarId ?: throw IllegalArgumentException(),
        name = title.getOrThrow(),
        startDate = startDate.getOrThrow(),
        endDate = endDate.getOrThrow(),
        notificationType = notificationType.getOrThrow(),
        memo = memo.getOrThrow(),
        color = tagColor.getOrThrow()
    )

    private fun getNotificationDate(): Date? {
        val calendar = Calendar.getInstance()
        calendar.time = startDate.getOrThrow()

        when (notificationType.value) {
            Schedule.NotificationType.NONE -> return null
            Schedule.NotificationType.TEN_MINUTES_AGO -> calendar.add(Calendar.MINUTE, -10)
            Schedule.NotificationType.A_HOUR_AGO -> calendar.add(Calendar.HOUR_OF_DAY, -1)
            Schedule.NotificationType.A_DAY_AGO -> calendar.add(Calendar.DAY_OF_YEAR, -1)
        }

        return calendar.time
    }
}
