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
            setUseDefaultCalendar()
        }
    }

    fun emitSaveCalendar() {
        viewModelScope.launch {
            _saveCalendarEvent.emit(saveCalendarInfo())
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
    
    private fun deleteCheckPointList(calendarId: Long) {
        viewModelScope.launch {
            checkPointLocalDataSource.deleteCheckPointList(calendarId)
        }
    }

    fun deleteCheckPointItem(currentCheckPointItemList: List<CheckPointItem>) {
        viewModelScope.launch {
            val newCheckPointItemList = currentCheckPointItemList.filter { checkPointItem -> !checkPointItem.check }
            _checkPointItemList.emit(newCheckPointItemList.toMutableList())
        }
    }
    
    private fun setUseDefaultCalendar() {
        viewModelScope.launch {
            useDefaultCalendar.emit(checkPointItemList.value.isEmpty())
        }
    }

    private fun emitBlankTitleEvent() {
        viewModelScope.launch {
            _blankTitleEvent.emit(Unit)
        }
    }


    private fun saveCalendarInfo(): Boolean {
        val useDefaultCalendar = useDefaultCalendar.value
        val calendarName = calendarName.value
        val checkPointList = _checkPointItemList.value
        var canSave = true

        if (calendarName.isBlank()) {
            emitBlankTitleEvent()
            canSave = false
        }

        if (useDefaultCalendar && canSave) {
            saveCalendar(calendarId, calendarName)
            deleteCheckPointList(calendarId)
            return true
        }

        var calendarStartDate = checkPointList.getOrNull(0)?.startDate?.value ?: LocalDate.now()
        var calendarEndDate = checkPointList.getOrNull(0)?.endDate?.value ?: LocalDate.now()

        checkPointList.forEach { item ->
            val name = item.name.value
            var startDate = item.startDate.value
            var endDate = item.endDate.value

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

            if (!isValidateCheckPointDate(startDate, endDate)) {
                emitBlankSliceStartDateEvent(item)
                emitBlankSliceEndDateEvent(item)
                canSave = false
            }

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

        val newCalendarId = saveCalendar(calendarId, calendarName, calendarStartDate, calendarEndDate)

        checkPointList.forEach { item ->
            saveCheckPoint(item, newCalendarId)
        }

        return true
    }

    private fun saveCalendar(id: Long, name: String, startDate: LocalDate = LocalDate.now(), endDate: LocalDate = LocalDate.now()): Long {
        var calendarId = id
        viewModelScope.launch {
            val newCalendar = Calendar(
                id = id,
                name = name,
                startDate = startDate,
                endDate = endDate
            )

            if (id != 0L) {
                calendarLocalDataSource.updateCalendar(newCalendar)
            } else {
                calendarId = calendarLocalDataSource.insertCalendar(newCalendar)
            }
        }
        return calendarId
    }


    private fun saveCheckPoint(item: CheckPointItem, newCalendarId: Long) {
        val newCheckPoint = CheckPoint(
            id = item.id,
            calendarId = newCalendarId,
            name = item.name.value,
            startDate = item.startDate.value ?: return,
            endDate = item.endDate.value ?: return
        )

        viewModelScope.launch {
            if (item.id != 0L) {
                checkPointLocalDataSource.updateCheckPoint(newCheckPoint)
            } else {
                checkPointLocalDataSource.insertCheckPoint(newCheckPoint)
            }
        }
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
