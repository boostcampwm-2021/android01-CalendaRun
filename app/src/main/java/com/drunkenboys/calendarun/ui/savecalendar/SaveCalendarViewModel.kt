package com.drunkenboys.calendarun.ui.savecalendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import com.drunkenboys.calendarun.data.checkpoint.entity.CheckPoint
import com.drunkenboys.calendarun.data.checkpoint.local.CheckPointLocalDataSource
import com.drunkenboys.calendarun.ui.savecalendar.model.CheckPointItem
import com.drunkenboys.calendarun.util.localDateToString
import com.drunkenboys.calendarun.util.stringToLocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveCalendarViewModel @Inject constructor(
    private val calendarLocalDataSource: CalendarLocalDataSource,
    private val checkPointLocalDataSource: CheckPointLocalDataSource
) : ViewModel() {

    private val _calendar = MutableStateFlow<Calendar?>(null)
    val calendar: StateFlow<Calendar?> = _calendar

    private val _checkPointItemList = MutableStateFlow<MutableList<CheckPointItem>>(mutableListOf())
    val checkPointItemList: StateFlow<MutableList<CheckPointItem>> = _checkPointItemList

    val calendarName = MutableStateFlow("")

    private val _calendarStartDate = MutableStateFlow("")
    val calendarStartDate: StateFlow<String> = _calendarStartDate

    private val _calendarEndDate = MutableStateFlow("")
    val calendarEndDate: StateFlow<String> = _calendarEndDate

    private val _pickStartDateEvent = MutableSharedFlow<Unit>()
    val pickStartDateEvent: SharedFlow<Unit> = _pickStartDateEvent

    private val _pickEndDateEvent = MutableSharedFlow<Unit>()
    val pickEndDateEvent: SharedFlow<Unit> = _pickEndDateEvent

    private val _saveCalendarEvent = MutableSharedFlow<Boolean>()
    val saveCalendarEvent: SharedFlow<Boolean> = _saveCalendarEvent

    fun setCalendar(calendar: Calendar) {
        viewModelScope.launch {
            _calendar.emit(calendar)
        }
    }

    fun setCalendarStartDate(date: String) {
        viewModelScope.launch {
            _calendarStartDate.emit(date)
        }
    }

    fun setCalendarEndDate(date: String) {
        viewModelScope.launch {
            _calendarEndDate.emit(date)
        }
    }

    fun emitPickStartDate() {
        viewModelScope.launch {
            _pickStartDateEvent.emit(Unit)
        }
    }

    fun emitPickEndDate() {
        viewModelScope.launch {
            _pickEndDateEvent.emit(Unit)
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
            _checkPointItemList.emit(newList)
        }
    }

    fun fetchCalendar(id: Long) {
        viewModelScope.launch {
            val selectedCalendar = calendarLocalDataSource.fetchCalendar(id)

            _calendar.emit(selectedCalendar)
            calendarName.emit(selectedCalendar.name)
            _calendarStartDate.emit(localDateToString(selectedCalendar.startDate))
            _calendarEndDate.emit(localDateToString(selectedCalendar.endDate))
        }
    }

    private fun saveCalendar(): Boolean {
        val calendarName = calendarName.value
        val startDate = _calendarStartDate.value
        val endDate = _calendarEndDate.value

        if (!isValidateCalendarDate(startDate, endDate)) return false

        if (!_checkPointItemList.value.isNullOrEmpty()) {
            _checkPointItemList.value.forEach { item ->
                item.name.value
                val date = item.date.value
                if (!isValidateCheckPointDate(date, startDate, endDate)) return false
            }
        }

        viewModelScope.launch() {
            val newCalender = Calendar(
                id = 0,
                name = calendarName,
                startDate = stringToLocalDate(startDate),
                endDate = stringToLocalDate(endDate),
            )
            val calendarId = calendarLocalDataSource.insertCalendar(newCalender)

            _checkPointItemList.value.forEach { item ->
                val checkPointName = item.name.value
                val date = item.date.value
                checkPointLocalDataSource.insertCheckPoint(
                    CheckPoint(
                        id = 0,
                        calendarId = calendarId,
                        name = checkPointName,
                        date = stringToLocalDate(date)
                    )
                )
            }
        }
        return true
    }

    private fun isValidateCalendarDate(startDate: String, endDate: String) =
        stringToLocalDate(startDate).isBefore(stringToLocalDate(endDate))

    private fun isValidateCheckPointDate(checkPointDate: String, startDate: String, endDate: String) =
        stringToLocalDate(checkPointDate).isAfter(stringToLocalDate(startDate)) && stringToLocalDate(checkPointDate).isBefore(
            stringToLocalDate(endDate)
        )
}
