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

    // TODO 캘린더를 추가하는 로직
    // TODO 체크포인트를 추가하는 로직

    private val _calendar = MutableLiveData<Calendar>()
    val calendar: LiveData<Calendar> = _calendar

    private val _checkPointList = MutableLiveData<List<CheckPoint>>()
    val checkPointList: LiveData<List<CheckPoint>> = _checkPointList

    private val _calendarName = MutableLiveData<String>()
    val calendarName: LiveData<String> = _calendarName

    private val _calendarStartDate = MutableLiveData<String>()
    val calendarStartDate: LiveData<String> = _calendarStartDate

    private val _calendarEndDate = MutableLiveData<String>()
    val calendarEndDate: LiveData<String> = _calendarEndDate

    fun fetchCalendar(id: Int) {
        viewModelScope.launch {
            val selectedCalendar = calendarLocalDataSource.fetchCalendar(id)
            val selectedCheckPointList = checkPointLocalDataSource.fetchCalendarCheckPoints(id)

            _calendar.postValue(selectedCalendar)
            _calendarName.postValue(selectedCalendar.name)
            _calendarStartDate.postValue(toStringDateFormat(selectedCalendar.startDate))
            _calendarEndDate.postValue(toStringDateFormat(selectedCalendar.endDate))

            _checkPointList.postValue(selectedCheckPointList)
        }
    }

    fun setCalendarName(name: String) {
        viewModelScope.launch {
            _calendarName.postValue(name)
        }
    }

    fun setCalendarStartDate(date: String) {
        viewModelScope.launch {
            _calendarStartDate.postValue(date)
        }
    }

    fun setCalendarEndDate(date: String) {
        viewModelScope.launch {
            _calendarEndDate.postValue(date)
        }
    }

    fun saveCalendar() {
        viewModelScope.launch {
            calendarLocalDataSource.insertCalendar(_calendar.value ?: return@launch)
            (_checkPointList.value ?: return@launch).forEach { checkPoint ->
                checkPointLocalDataSource.insertCheckPoint(checkPoint)
            }
        }
    }
}
