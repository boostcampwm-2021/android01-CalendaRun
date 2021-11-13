package com.drunkenboys.calendarun.ui.saveschedule

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.KEY_CALENDAR_ID
import com.drunkenboys.calendarun.KEY_LOCAL_DATE
import com.drunkenboys.calendarun.KEY_SCHEDULE_ID
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.ui.saveschedule.model.DateType
import com.drunkenboys.ckscalendar.data.ScheduleColorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SaveScheduleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val calendarDataSource: CalendarLocalDataSource,
    private val scheduleDataSource: ScheduleLocalDataSource
) : ViewModel() {

    private val calendarId = savedStateHandle[KEY_CALENDAR_ID] ?: 0L
    private val scheduleId = savedStateHandle[KEY_SCHEDULE_ID] ?: 0L
    private val localDateTime = savedStateHandle.get<String>(KEY_LOCAL_DATE)?.let {
        LocalDateTime.of(LocalDate.parse(it), LocalTime.of(12, 0))
    } ?: run { LocalDateTime.now().withMinute(0).withSecond(0) }

    private val isUpdateSchedule = scheduleId > 0

    val title = MutableStateFlow("")

    private val _startDate = MutableStateFlow<LocalDateTime>(localDateTime)
    val startDate: StateFlow<LocalDateTime> = _startDate

    private val _endDate = MutableStateFlow<LocalDateTime>(localDateTime)
    val endDate: StateFlow<LocalDateTime> = _endDate

    val memo = MutableStateFlow("")

    private val _calendarName = MutableStateFlow("")
    val calendarName: StateFlow<String> = _calendarName

    private val _notificationType = MutableStateFlow(Schedule.NotificationType.TEN_MINUTES_AGO)
    val notificationType: StateFlow<Schedule.NotificationType> = _notificationType

    private val _tagColor = MutableStateFlow(ScheduleColorType.RED.color)
    val tagColor: StateFlow<Int> = _tagColor

    private val _isPickTagColorPopupVisible = MutableStateFlow(false)
    val isPickTagColorPopupVisible: StateFlow<Boolean> = _isPickTagColorPopupVisible

    private val _pickDateTimeEvent = MutableSharedFlow<DateType>()
    val pickDateTimeEvent: SharedFlow<DateType> = _pickDateTimeEvent

    private val _pickNotificationTypeEvent = MutableSharedFlow<Unit>()
    val pickNotificationTypeEvent: SharedFlow<Unit> = _pickNotificationTypeEvent

    private val _saveScheduleEvent = MutableSharedFlow<Schedule>()
    val saveScheduleEvent: SharedFlow<Schedule> = _saveScheduleEvent

    private val _deleteScheduleEvent = MutableSharedFlow<Schedule>()
    val deleteScheduleEvent: SharedFlow<Schedule> = _deleteScheduleEvent

    init {
        initCalendarName()
        restoreScheduleData()
    }

    private fun initCalendarName() {
        viewModelScope.launch {
            _calendarName.emit(calendarDataSource.fetchCalendar(calendarId).name)
        }
    }

    private fun restoreScheduleData() {
        if (!isUpdateSchedule) return

        viewModelScope.launch {
            val schedule = scheduleDataSource.fetchSchedule(scheduleId)

            title.emit(schedule.name)
            _startDate.emit(schedule.startDate)
            _endDate.emit(schedule.endDate)
            memo.emit(schedule.memo)
            _notificationType.emit(schedule.notificationType)
            _tagColor.emit(schedule.color)
        }
    }

    fun emitPickDateTimeEvent(dateType: DateType) {
        viewModelScope.launch {
            _pickDateTimeEvent.emit(dateType)
        }
    }

    fun updateDate(date: LocalDateTime, dateType: DateType) {
        viewModelScope.launch {
            when (dateType) {
                DateType.START -> {
                    if (date.isAfter(endDate.value)) _endDate.emit(date)
                    _startDate.emit(date)
                }
                DateType.END -> {
                    if (date.isBefore(startDate.value)) _startDate.emit(date)
                    _endDate.emit(date)
                }
            }
        }
    }

    fun LocalDateTime.toFormatString(): String {
        val amPm = if (hour < 12) "오전" else "오후"
        val dateFormat = DateTimeFormatter.ofPattern("MM월 dd일 $amPm hh:mm")

        return format(dateFormat)
    }

    fun emitPickNotificationTypeEvent() {
        viewModelScope.launch {
            _pickNotificationTypeEvent.emit(Unit)
        }
    }

    fun updateNotificationType(notificationType: Schedule.NotificationType) {
        viewModelScope.launch {
            _notificationType.emit(notificationType)
        }
    }

    fun togglePickTagColorPopup() {
        viewModelScope.launch {
            _isPickTagColorPopupVisible.emit(!_isPickTagColorPopupVisible.value)
        }
    }

    fun pickTagColor(tagColor: Int) {
        viewModelScope.launch {
            _tagColor.emit(tagColor)
            _isPickTagColorPopupVisible.emit(false)
        }
    }

    fun saveSchedule() {
        if (isInvalidInput()) return

        viewModelScope.launch {
            val schedule = createScheduleInstance()

            if (isUpdateSchedule) {
                scheduleDataSource.updateSchedule(schedule)
                _saveScheduleEvent.emit(schedule)
            } else {
                val rowId = scheduleDataSource.insertSchedule(schedule)
                _saveScheduleEvent.emit(schedule.copy(id = rowId))
            }
        }
    }

    private fun isInvalidInput(): Boolean {
        if (title.value.isEmpty()) return true
        if (calendarName.value.isEmpty()) return true

        return false
    }

    private fun createScheduleInstance() = Schedule(
        id = scheduleId,
        calendarId = calendarId,
        name = title.value,
        startDate = startDate.value,
        endDate = endDate.value,
        notificationType = notificationType.value,
        memo = memo.value,
        color = tagColor.value
    )

    fun deleteSchedule() {
        if (scheduleId < 0) return

        viewModelScope.launch {
            val deleteSchedule = scheduleDataSource.fetchSchedule(scheduleId)
            scheduleDataSource.deleteSchedule(deleteSchedule)

            _deleteScheduleEvent.emit(deleteSchedule)
        }
    }
}
