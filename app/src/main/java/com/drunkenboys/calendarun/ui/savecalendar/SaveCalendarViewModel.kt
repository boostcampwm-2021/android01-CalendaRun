package com.drunkenboys.calendarun.ui.savecalendar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.KEY_CALENDAR_ID
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import com.drunkenboys.calendarun.data.checkpoint.entity.CheckPoint
import com.drunkenboys.calendarun.data.checkpoint.local.CheckPointLocalDataSource
import com.drunkenboys.calendarun.ui.savecalendar.model.CheckPointItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SaveCalendarViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val calendarLocalDataSource: CalendarLocalDataSource,
    private val checkPointLocalDataSource: CheckPointLocalDataSource
) : ViewModel() {

    private val calendarId = savedStateHandle[KEY_CALENDAR_ID] ?: 0L

    private val _checkPointItemList = MutableStateFlow(listOf(CheckPointItem()))
    val checkPointItemList: StateFlow<List<CheckPointItem>> = _checkPointItemList

    val calendarName = MutableStateFlow("")

    val useDefaultCalendar = MutableStateFlow(false)

    private val _saveCalendarEvent = MutableSharedFlow<Boolean>()
    val saveCalendarEvent: SharedFlow<Boolean> = _saveCalendarEvent

    private val _blankTitleEvent = MutableSharedFlow<Unit>()
    val blankTitleEvent: SharedFlow<Unit> = _blankTitleEvent

    init {
        viewModelScope.launch {
            val calendar = calendarLocalDataSource.fetchCalendar(calendarId) ?: return@launch

            calendarName.emit(calendar.name)

            val checkPointItemList = mutableListOf<CheckPointItem>()
            val checkPointList = checkPointLocalDataSource.fetchCalendarCheckPoints(calendarId)
            checkPointList.forEach { checkPoint ->
                checkPointItemList.add(
                    CheckPointItem(
                        id = checkPoint.id,
                        name = MutableStateFlow(checkPoint.name),
                        startDate = MutableStateFlow(checkPoint.startDate),
                        endDate = MutableStateFlow(checkPoint.endDate)
                    )
                )
            }
            _checkPointItemList.emit(checkPointItemList)
        }
    }

    fun emitSaveCalendar() {
        viewModelScope.launch {
            _saveCalendarEvent.emit(saveCalendar())
        }
    }

    fun addCheckPoint() {
        viewModelScope.launch {
            val newList = mutableListOf<CheckPointItem>()
            newList.addAll(checkPointItemList.value)
            newList.add(CheckPointItem())
            _checkPointItemList.emit(newList.toList())
        }
    }

    fun deleteCheckPointItem(currentCheckPointItemList: List<CheckPointItem>) {
        viewModelScope.launch {
            val newCheckPointItemList = currentCheckPointItemList.filter { checkPointItem -> !checkPointItem.check }
            _checkPointItemList.emit(newCheckPointItemList.toMutableList())
        }
    }

    private fun emitBlankTitleEvent() {
        viewModelScope.launch {
            _blankTitleEvent.emit(Unit)
        }
    }

    private fun saveCalendar(): Boolean {
        val useDefaultCalendar = useDefaultCalendar.value
        val calendarName = calendarName.value
        var canSave = true

        if (calendarName.isEmpty()) {
            emitBlankTitleEvent()
            canSave = false
        }

        if (useDefaultCalendar && canSave) {
            viewModelScope.launch {
                calendarLocalDataSource.insertCalendar(
                    Calendar(
                        id = calendarId,
                        name = calendarName,
                        startDate = LocalDate.now(),
                        endDate = LocalDate.now()
                    )
                )
            }
            return true
        }

        val checkPointList = _checkPointItemList.value
        var calendarStartDate = checkPointList.getOrNull(0)?.startDate?.value ?: LocalDate.now()
        var calendarEndDate = checkPointList.getOrNull(0)?.endDate?.value ?: LocalDate.now()

        checkPointList.forEach { item ->
            val name = item.name.value
            val startDate = item.startDate.value
            val endDate = item.endDate.value

            if (name.isBlank()) {
                emitBlankSliceNameEvent(item)
                canSave = false
            }
            if (startDate == null) {
                emitBlankSliceStartDateEvent(item)
                canSave = false
            }
            if (endDate == null) {
                emitBlankSliceEndDateEvent(item)
                canSave = false
            }

            if (!isValidateCheckPointDate(startDate, endDate)) return false

            startDate?.let {
                if (calendarStartDate.isAfter(it)) {
                    calendarStartDate = it
                }
            }

            endDate?.let {
                if (calendarEndDate.isBefore(it)) {
                    calendarEndDate = it
                }
            }
        }

        if (!canSave) return false

        viewModelScope.launch() {
            val newCalender = Calendar(
                id = calendarId,
                name = calendarName,
                startDate = calendarStartDate,
                endDate = calendarEndDate
            )
            val calendarId = calendarLocalDataSource.insertCalendar(newCalender)
            checkPointList.forEach { item ->
                val id = item.id
                val checkPointName = item.name.value
                val startDate = item.startDate.value ?: return@launch
                val endDate = item.endDate.value ?: return@launch

                checkPointLocalDataSource.insertCheckPoint(
                    CheckPoint(
                        id = id,
                        calendarId = calendarId,
                        name = checkPointName,
                        startDate = startDate,
                        endDate = endDate
                    )
                )
            }
        }
        return true
    }

    private fun emitBlankSliceNameEvent(item: CheckPointItem) {
        viewModelScope.launch {
            item.isNameBlank.emit(Unit)
        }
    }

    private fun emitBlankSliceStartDateEvent(item: CheckPointItem) {
        viewModelScope.launch {
            item.isStartDateBlank.emit(Unit)
        }
    }

    private fun emitBlankSliceEndDateEvent(item: CheckPointItem) {
        viewModelScope.launch {
            item.isEndDateBlank.emit(Unit)
        }
    }

    private fun isValidateCheckPointDate(startDate: LocalDate?, endDate: LocalDate?) = startDate?.isBefore(endDate) ?: true
}
