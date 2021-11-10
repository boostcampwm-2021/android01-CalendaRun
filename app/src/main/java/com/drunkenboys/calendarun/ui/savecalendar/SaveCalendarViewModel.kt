package com.drunkenboys.calendarun.ui.savecalendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import com.drunkenboys.calendarun.data.checkpoint.entity.CheckPoint
import com.drunkenboys.calendarun.data.checkpoint.local.CheckPointLocalDataSource
import com.drunkenboys.calendarun.ui.savecalendar.model.CheckPointItem
import com.drunkenboys.calendarun.util.SingleLiveEvent
import com.drunkenboys.calendarun.util.dateToString
import com.drunkenboys.calendarun.util.stringToDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveCalendarViewModel @Inject constructor(
    private val calendarLocalDataSource: CalendarLocalDataSource,
    private val checkPointLocalDataSource: CheckPointLocalDataSource
) : ViewModel() {

    private val _calendar = MutableLiveData<Calendar>()
    val calendar: LiveData<Calendar> = _calendar

    private val _checkPointItemList = MutableLiveData<MutableList<CheckPointItem>>()
    val checkPointItemList: LiveData<MutableList<CheckPointItem>> = _checkPointItemList

    val calendarName = MutableLiveData<String>()

    private val _calendarStartDate = MutableLiveData<String>()
    val calendarStartDate: LiveData<String> = _calendarStartDate

    private val _calendarEndDate = MutableLiveData<String>()
    val calendarEndDate: LiveData<String> = _calendarEndDate

    private val _pickStartDateEvent = SingleLiveEvent<Unit>()
    val pickStartDateEvent: LiveData<Unit> = _pickStartDateEvent

    private val _pickEndDateEvent = SingleLiveEvent<Unit>()
    val pickEndDateEvent: LiveData<Unit> = _pickEndDateEvent

    private val _saveCalendarEvent = SingleLiveEvent<Boolean>()
    val saveCalendarEvent: LiveData<Boolean> = _saveCalendarEvent

    fun setCalendar(calendar: Calendar) {
        _calendar.value = calendar
    }

    fun setCalendarStartDate(date: String) {
        _calendarStartDate.value = date
    }

    fun setCalendarEndDate(date: String) {
        _calendarEndDate.value = date
    }

    fun emitPickStartDate() {
        _pickStartDateEvent.value = Unit
    }

    fun emitPickEndDate() {
        _pickEndDateEvent.value = Unit
    }

    fun emitSaveCalendar() {
        _saveCalendarEvent.value = saveCalendar()
    }

    fun addCheckPoint() {
        val newList = mutableListOf<CheckPointItem>()
        newList.addAll(checkPointItemList.value ?: mutableListOf())
        newList.add(CheckPointItem())
        _checkPointItemList.value = newList
    }

    fun fetchCalendar(id: Long) {
        viewModelScope.launch {
            val selectedCalendar = calendarLocalDataSource.fetchCalendar(id)

            _calendar.value = selectedCalendar
            calendarName.value = selectedCalendar.name
            _calendarStartDate.value = dateToString(selectedCalendar.startDate)
            _calendarEndDate.value = dateToString(selectedCalendar.endDate)
        }
    }

    private fun saveCalendar(): Boolean {
        val calendarName = calendarName.value ?: return false
        val startDate = _calendarStartDate.value ?: return false
        val endDate = _calendarEndDate.value ?: return false

        if (!isValidateCalendarDate(startDate, endDate)) return false

        if (!_checkPointItemList.value.isNullOrEmpty()) {
            _checkPointItemList.value?.forEach { item ->
                item.name.value ?: return false
                val date = item.date.value ?: return false
                if (!isValidateCheckPointDate(date, startDate, endDate)) return false
            }
        }

        viewModelScope.launch() {
            val newCalender = Calendar(
                id = 0,
                name = calendarName,
                startDate = stringToDate(startDate),
                endDate = stringToDate(endDate),
            )
            val calendarId = calendarLocalDataSource.insertCalendar(newCalender)

            _checkPointItemList.value?.forEach { item ->
                val checkPointName = item.name.value ?: return@launch
                val date = item.date.value ?: return@launch
                checkPointLocalDataSource.insertCheckPoint(
                    CheckPoint(
                        id = 0,
                        calendarId = calendarId,
                        name = checkPointName,
                        date = stringToDate(date)
                    )
                )
            }
        }
        return true
    }


    private fun isValidateCalendarDate(startDate: String, endDate: String) = stringToDate(startDate) < stringToDate(endDate)

    private fun isValidateCheckPointDate(checkPointDate: String, startDate: String, endDate: String) =
        stringToDate(checkPointDate).after(stringToDate(startDate)) && stringToDate(checkPointDate).before(stringToDate(endDate))
}
