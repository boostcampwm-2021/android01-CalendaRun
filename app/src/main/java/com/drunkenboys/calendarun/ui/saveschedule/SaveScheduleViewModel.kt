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
import com.drunkenboys.calendarun.util.DateFormatLimitType
import com.drunkenboys.calendarun.util.relativeDateFormat
import com.drunkenboys.ckscalendar.data.ScheduleColorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
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

    val calendarName = flow {
        emit(calendarDataSource.fetchCalendar(calendarId).name)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    private val _notificationType = MutableStateFlow(Schedule.NotificationType.TEN_MINUTES_AGO)
    val notificationType: StateFlow<Schedule.NotificationType> = _notificationType

    private val _tagColor = MutableStateFlow(ScheduleColorType.RED.color)
    val tagColor: StateFlow<Int> = _tagColor

    private val _isPickTagColorPopupVisible = MutableStateFlow(false)
    val isPickTagColorPopupVisible: StateFlow<Boolean> = _isPickTagColorPopupVisible

    private val _pickDateTimeEvent = MutableSharedFlow<Pair<DateType, LocalDateTime>>()
    val pickDateTimeEvent: SharedFlow<Pair<DateType, LocalDateTime>> = _pickDateTimeEvent

    private val _pickNotificationTypeEvent = MutableSharedFlow<Unit>()
    val pickNotificationTypeEvent: SharedFlow<Unit> = _pickNotificationTypeEvent

    private val _saveScheduleEvent = MutableSharedFlow<Pair<Schedule, String>>()
    val saveScheduleEvent: SharedFlow<Pair<Schedule, String>> = _saveScheduleEvent

    private val _deleteScheduleEvent = MutableSharedFlow<Pair<Schedule, String>>()
    val deleteScheduleEvent: SharedFlow<Pair<Schedule, String>> = _deleteScheduleEvent

    private val _blankTitleEvent = MutableSharedFlow<Unit>()
    val blankTitleEvent: SharedFlow<Unit> = _blankTitleEvent

    private val _openDeleteDialog = MutableSharedFlow<Int>()
    val openDeleteDialog: SharedFlow<Int> = _openDeleteDialog

    init {
        restoreScheduleData()
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
            when (dateType) {
                DateType.START -> _pickDateTimeEvent.emit(dateType to startDate.value)
                DateType.END -> _pickDateTimeEvent.emit(dateType to endDate.value)
            }
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

    fun LocalDateTime.toFormatString(): String = format(relativeDateFormat(LocalDateTime.now(), this, DateFormatLimitType.MONTH))

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
        viewModelScope.launch {
            if (title.value.isBlank()) {
                _blankTitleEvent.emit(Unit)
                return@launch
            }

            val schedule = createScheduleInstance()

            if (isUpdateSchedule) {
                scheduleDataSource.updateSchedule(schedule)
                _saveScheduleEvent.emit(schedule to calendarName.value)
            } else {
                val rowId = scheduleDataSource.insertSchedule(schedule)
                _saveScheduleEvent.emit(schedule.copy(id = rowId) to calendarName.value)
            }
        }
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
        if (!isUpdateSchedule) return

        viewModelScope.launch {
            val deleteSchedule = scheduleDataSource.fetchSchedule(scheduleId)
            scheduleDataSource.deleteSchedule(deleteSchedule)

            _deleteScheduleEvent.emit(deleteSchedule to calendarName.value)
        }
    }

    fun emitOpenDeleteDialog(id: Int) {
        viewModelScope.launch {
            _openDeleteDialog.emit(id)
        }
    }
}
