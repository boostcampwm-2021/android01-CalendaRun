package com.drunkenboys.calendarun.ui.addcalendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import com.drunkenboys.calendarun.data.checkpoint.entity.CheckPoint
import com.drunkenboys.calendarun.data.checkpoint.local.CheckPointLocalDataSource
import com.drunkenboys.calendarun.toStringDateFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCalendarViewModel @Inject constructor(
    private val calendarLocalDataSource: CalendarLocalDataSource,
    private val checkPointLocalDataSource: CheckPointLocalDataSource
) : ViewModel() {

    private val _calendar = MutableLiveData<Calendar>()
    val calendar: LiveData<Calendar> = _calendar

    private val _checkPointList = MutableLiveData<List<CheckPoint>>()
    val checkPointList: LiveData<List<CheckPoint>> = _checkPointList

    val calendarName = MutableLiveData<String>()

    private val _calendarStartDate = MutableLiveData<String>()
    val calendarStartDate: LiveData<String> = _calendarStartDate

    private val _calendarEndDate = MutableLiveData<String>()
    val calendarEndDate: LiveData<String> = _calendarEndDate

    fun setCalendarStartDate(date: String) {
        _calendarStartDate.value = date
    }

    fun setCalendarEndDate(date: String) {
        _calendarEndDate.value = date
    }

    fun fetchCalendar(id: Int) {
        viewModelScope.launch {
            val selectedCalendar = calendarLocalDataSource.fetchCalendar(id)
            val selectedCheckPointList = checkPointLocalDataSource.fetchCalendarCheckPoints(id)

            _calendar.value = selectedCalendar
            calendarName.value = selectedCalendar.name
            _calendarStartDate.value = toStringDateFormat(selectedCalendar.startDate)
            _calendarEndDate.value = toStringDateFormat(selectedCalendar.endDate)

            _checkPointList.value = selectedCheckPointList
        }
    }

    fun saveCalendar() {
        viewModelScope.launch {
            calendarLocalDataSource.insertCalendar(_calendar.value ?: return@launch)
            _checkPointList.value?.forEach { checkPoint ->
                checkPointLocalDataSource.insertCheckPoint(checkPoint)
            }
        }
    }
}
