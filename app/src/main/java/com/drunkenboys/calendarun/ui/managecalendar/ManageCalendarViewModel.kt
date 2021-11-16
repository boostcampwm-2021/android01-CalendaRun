package com.drunkenboys.calendarun.ui.managecalendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageCalendarViewModel @Inject constructor(
    private val calendarLocalDataSource: CalendarLocalDataSource
) : ViewModel() {

    private val _calendarList = MutableStateFlow<List<Calendar>>(emptyList())
    val calendarList: StateFlow<List<Calendar>> = _calendarList

    fun fetchCalendarList() {
        viewModelScope.launch {
            _calendarList.emit(calendarLocalDataSource.fetchAllCalendar())
        }
    }
}
