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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveCalendarViewModel @Inject constructor(
    private val calendarLocalDataSource: CalendarLocalDataSource,
    private val checkPointLocalDataSource: CheckPointLocalDataSource
) : ViewModel() {

    private val _calendar = MutableLiveData<Calendar>()
    val calendar: LiveData<Calendar> = _calendar

    /*private val _checkPointList = MutableLiveData<List<CheckPoint>>()
    val checkPointList: LiveData<List<CheckPoint>> = _checkPointList*/

    private val _checkPointItemList = MutableLiveData<List<CheckPointItem>>()
    val checkPointItemList: LiveData<List<CheckPointItem>> = _checkPointItemList

    val calendarName = MutableLiveData<String>()

    private val _calendarStartDate = MutableLiveData<String>()
    val calendarStartDate: LiveData<String> = _calendarStartDate

    private val _calendarEndDate = MutableLiveData<String>()
    val calendarEndDate: LiveData<String> = _calendarEndDate

    private val _pickStartDateEvent = SingleLiveEvent<Unit>()
    val pickStartDateEvent: LiveData<Unit> = _pickStartDateEvent

    private val _pickEndDateEvent = SingleLiveEvent<Unit>()
    val pickEndDateEvent: LiveData<Unit> = _pickEndDateEvent

    private val _saveCalendarEvent = SingleLiveEvent<Unit>()
    val saveCalendarEvent: LiveData<Unit> = _saveCalendarEvent

    fun setCalendar(calendar: Calendar) {
        _calendar.value = calendar
    }

    fun setCheckPointItemList(checkPointItemList: List<CheckPointItem>) {
        _checkPointItemList.value = checkPointItemList
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
        _saveCalendarEvent.value = Unit
    }

    fun fetchCalendar(id: Int) {
        viewModelScope.launch {
            val selectedCalendar = calendarLocalDataSource.fetchCalendar(id)
            val selectedCheckPointList = checkPointLocalDataSource.fetchCalendarCheckPoints(id)

            _calendar.value = selectedCalendar
            calendarName.value = selectedCalendar.name
            _calendarStartDate.value = dateToString(selectedCalendar.startDate)
            _calendarEndDate.value = dateToString(selectedCalendar.endDate)
        }
    }

    suspend fun saveCalendar() = viewModelScope.async {
        val calendarName = calendarName.value ?: return@async false
        val startDate = _calendarStartDate.value ?: return@async false
        val endDate = _calendarEndDate.value ?: return@async false

        if (!isValidateCalendarDate(startDate, endDate)) return@async false
        val newCalender = Calendar(
            id = 0,
            name = calendarName,
            startDate = stringToDate(startDate),
            endDate = stringToDate(endDate),
        )

        if (!_checkPointItemList.value.isNullOrEmpty()) {
            _checkPointItemList.value?.forEach { item ->
                item.name.value ?: return@async false
                val date = item.date.value ?: return@async false
                if (!isValidateCheckPointDate(date, startDate, endDate)) return@async false
            }
        }

        val calendarId = calendarLocalDataSource.insertCalendar(newCalender)
        _checkPointItemList.value?.forEach { item ->
            val checkPointName = item.name.value ?: return@async false
            val date = item.date.value ?: return@async false
            checkPointLocalDataSource.insertCheckPoint(
                CheckPoint(
                    id = 0,
                    calendarId = calendarId,
                    name = checkPointName,
                    date = stringToDate(date)
                )
            )
        }
        true
    }.await()

    private fun isValidateCalendarDate(startDate: String, endDate: String) = stringToDate(startDate) < stringToDate(endDate)

    private fun isValidateCheckPointDate(checkPointDate: String, startDate: String, endDate: String) =
        stringToDate(startDate) < stringToDate(checkPointDate) && stringToDate(checkPointDate) < stringToDate(endDate)
}
