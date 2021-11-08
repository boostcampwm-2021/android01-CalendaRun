package com.drunkenboys.calendarun.ui.maincalendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCalendarViewModel @Inject constructor(
    private val calendarLocalDataSource: CalendarLocalDataSource
) : ViewModel() {

    private val _calendar = MutableLiveData<Calendar>()
    val calendar: LiveData<Calendar> = _calendar

    private val _calendarList = MutableLiveData<List<Calendar>>()
    val calendarList: LiveData<List<Calendar>> = _calendarList

    fun setMainCalendar(calendar: Calendar) {
        _calendar.value = calendar
    }

    fun fetchCalendarList() {
        viewModelScope.launch {
            _calendarList.value = calendarLocalDataSource.fetchAllCalendar()
        }
    }
}
