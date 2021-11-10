package com.drunkenboys.calendarun.ui.saveschedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.di.CalendarId
import com.drunkenboys.calendarun.di.ScheduleId
import com.drunkenboys.calendarun.ui.saveschedule.model.BehaviorType
import com.drunkenboys.calendarun.ui.saveschedule.model.DateType
import com.drunkenboys.calendarun.util.SingleLiveEvent
import com.drunkenboys.calendarun.util.extensions.getOrThrow
import com.drunkenboys.ckscalendar.data.ScheduleColorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SaveScheduleViewModel @Inject constructor(
    @CalendarId private val calendarId: Long,
    @ScheduleId private val scheduleId: Long,
    private val calendarDataSource: CalendarLocalDataSource,
    private val scheduleDataSource: ScheduleLocalDataSource
) : ViewModel() {

    private lateinit var behaviorType: BehaviorType

    val title = MutableLiveData("")

    private val _startDate = MutableLiveData<LocalDateTime>()
    val startDate: LiveData<LocalDateTime> = _startDate

    private val _endDate = MutableLiveData<LocalDateTime>()
    val endDate: LiveData<LocalDateTime> = _endDate

    val memo = MutableLiveData("")

    private val _calendarName = MutableLiveData("test")
    val calendarName: LiveData<String> = _calendarName

    val notificationType = MutableLiveData(Schedule.NotificationType.TEN_MINUTES_AGO)

    private val _tagColor = MutableLiveData(ScheduleColorType.RED.color)
    val tagColor: LiveData<Int> = _tagColor

    private val _saveScheduleEvent = SingleLiveEvent<Schedule>()
    val saveScheduleEvent: LiveData<Schedule> = _saveScheduleEvent

    private val _deleteScheduleEvent = SingleLiveEvent<Schedule>()
    val deleteScheduleEvent: LiveData<Schedule> = _deleteScheduleEvent

    private val _pickDateTimeEvent = SingleLiveEvent<DateType>()
    val pickDateTimeEvent: LiveData<DateType> = _pickDateTimeEvent

    private val _pickNotificationTypeEvent = SingleLiveEvent<Unit>()
    val pickNotificationTypeEvent: LiveData<Unit> = _pickNotificationTypeEvent

    private val _isPickTagColorPopupVisible = MutableLiveData(false)
    val isPickTagColorPopupVisible: LiveData<Boolean> = _isPickTagColorPopupVisible

    fun init(behaviorType: BehaviorType, localDate: String? = null) {
        initCalendarName()
        initScheduleDateTime(localDate)
        this.behaviorType = behaviorType

        if (behaviorType == BehaviorType.UPDATE) initData()
    }

    private fun initCalendarName() {
        viewModelScope.launch {
            _calendarName.value = calendarDataSource.fetchCalendar(calendarId).name
        }
    }

    private fun initScheduleDateTime(localDate: String?) {
        val localDateTime = if (localDate == null) {
            LocalDateTime.now()
                .withMinute(0)
                .withSecond(0)
        } else {
            LocalDateTime.of(LocalDate.parse(localDate), LocalTime.of(12, 0))
        }
        _startDate.value = localDateTime
        _endDate.value = localDateTime
    }

    private fun initData() {
        viewModelScope.launch {
            val schedule = scheduleDataSource.fetchSchedule(scheduleId)

            title.value = schedule.name
            _startDate.value = schedule.startDate
            _endDate.value = schedule.endDate
            memo.value = schedule.memo
            notificationType.value = schedule.notificationType
            _tagColor.value = schedule.color
        }
    }

    fun emitPickDateTimeEvent(dateType: DateType) {
        _pickDateTimeEvent.value = dateType
    }

    fun emitPickNotificationTypeEvent() {
        _pickNotificationTypeEvent.value = Unit
    }

    fun updateDate(date: LocalDateTime, dateType: DateType) {
        when (dateType) {
            DateType.START -> {
                if (date.isAfter(endDate.getOrThrow())) _endDate.value = date
                _startDate.value = date
            }
            DateType.END -> {
                if (date.isBefore(startDate.getOrThrow())) _startDate.value = date
                _endDate.value = date
            }
        }
    }

    fun LocalDateTime.toFormatString(): String {
        val amPm = if (hour < 12) "오전" else "오후"
        val dateFormat = DateTimeFormatter.ofPattern("MM월 dd일 $amPm hh:mm")

        return format(dateFormat)
    }

    fun saveSchedule() {
        if (isInvalidInput()) return

        viewModelScope.launch {
            val schedule = createScheduleInstance()

            when (behaviorType) {
                BehaviorType.INSERT -> {
                    val rowId = scheduleDataSource.insertSchedule(schedule)
                    _saveScheduleEvent.value = schedule.copy(id = rowId)
                }
                BehaviorType.UPDATE -> {
                    scheduleDataSource.updateSchedule(schedule)
                    _saveScheduleEvent.value = schedule
                }
            }
        }
    }

    private fun isInvalidInput(): Boolean {
        if (!this::behaviorType.isInitialized) return true
        if (title.value.isNullOrEmpty()) return true
        startDate.value ?: return true
        endDate.value ?: return true
        if (calendarName.value.isNullOrEmpty()) return true

        return false
    }

    private fun createScheduleInstance() = Schedule(
        id = scheduleId,
        calendarId = calendarId,
        name = title.getOrThrow(),
        startDate = startDate.getOrThrow(),
        endDate = endDate.getOrThrow(),
        notificationType = notificationType.getOrThrow(),
        memo = memo.getOrThrow(),
        color = tagColor.getOrThrow()
    )

    fun deleteSchedule() {
        if (scheduleId < 0) return

        viewModelScope.launch {
            val deleteSchedule = scheduleDataSource.fetchSchedule(scheduleId)
            scheduleDataSource.deleteSchedule(deleteSchedule)

            _deleteScheduleEvent.value = deleteSchedule
        }
    }

    fun togglePickTagColorPopup() {
        _isPickTagColorPopupVisible.value = !_isPickTagColorPopupVisible.getOrThrow()
    }

    fun pickTagColor(tagColor: Int) {
        _tagColor.value = tagColor
        _isPickTagColorPopupVisible.value = false
    }
}
