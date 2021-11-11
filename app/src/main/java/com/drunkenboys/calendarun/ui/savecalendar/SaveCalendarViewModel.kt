package com.drunkenboys.calendarun.ui.savecalendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import com.drunkenboys.calendarun.data.checkpoint.entity.CheckPoint
import com.drunkenboys.calendarun.data.checkpoint.local.CheckPointLocalDataSource
import com.drunkenboys.calendarun.ui.savecalendar.model.CheckPointItem
import com.drunkenboys.calendarun.ui.saveschedule.model.DateType
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
    private val calendarLocalDataSource: CalendarLocalDataSource,
    private val checkPointLocalDataSource: CheckPointLocalDataSource
) : ViewModel() {

    private val _calendar = MutableStateFlow<Calendar?>(null)
    val calendar: StateFlow<Calendar?> = _calendar

    private val _checkPointItemList = MutableStateFlow<MutableList<CheckPointItem>>(mutableListOf())
    val checkPointItemList: StateFlow<MutableList<CheckPointItem>> = _checkPointItemList

    val calendarName = MutableStateFlow("")

    private val _calendarStartDate = MutableStateFlow<LocalDate?>(null)
    val calendarStartDate: StateFlow<LocalDate?> = _calendarStartDate

    private val _calendarEndDate = MutableStateFlow<LocalDate?>(null)
    val calendarEndDate: StateFlow<LocalDate?> = _calendarEndDate

    private val _pickDateEvent = MutableSharedFlow<DateType>()
    val pickDateEvent: SharedFlow<DateType> = _pickDateEvent

    private val _saveCalendarEvent = MutableSharedFlow<Boolean>()
    val saveCalendarEvent: SharedFlow<Boolean> = _saveCalendarEvent

    fun setCalendar(calendar: Calendar) {
        viewModelScope.launch {
            _calendar.emit(calendar)
        }
    }

    fun emitPickDate(dateType: DateType) {
        viewModelScope.launch {
            _pickDateEvent.emit(dateType)
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

    fun updateDate(date: LocalDate, dateType: DateType) {
        viewModelScope.launch {
            when (dateType) {
                DateType.START -> _calendarStartDate.emit(date)
                DateType.END -> _calendarEndDate.emit(date)
            }
        }
    }

    private fun saveCalendar(): Boolean {
        val calendarName = calendarName.value
        val startDate = _calendarStartDate.value ?: return false
        val endDate = _calendarEndDate.value ?: return false

        if (!isValidateCalendarDate(startDate, endDate)) return false

        if (!_checkPointItemList.value.isNullOrEmpty()) {
            _checkPointItemList.value.forEach { item ->
                item.name.value
                val date = item.date.value ?: return false
                if (!isValidateCheckPointDate(date, startDate, endDate)) return false
            }
        }

        viewModelScope.launch() {
            val newCalender = Calendar(
                id = 0,
                name = calendarName,
                startDate = startDate,
                endDate = endDate,
            )
            val calendarId = calendarLocalDataSource.insertCalendar(newCalender)

            _checkPointItemList.value.forEach { item ->
                val checkPointName = item.name.value
                val date = item.date.value ?: return@launch
                checkPointLocalDataSource.insertCheckPoint(
                    CheckPoint(
                        id = 0,
                        calendarId = calendarId,
                        name = checkPointName,
                        date = date
                    )
                )
            }
        }
        return true
    }

    private fun isValidateCalendarDate(startDate: LocalDate, endDate: LocalDate) = startDate.isBefore(endDate)

    private fun isValidateCheckPointDate(checkPointDate: LocalDate, startDate: LocalDate, endDate: LocalDate) =
        checkPointDate.isAfter(startDate) && checkPointDate.isBefore(endDate)
}
