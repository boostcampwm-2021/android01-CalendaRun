package com.drunkenboys.calendarun.ui.managecalendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import com.drunkenboys.calendarun.ui.managecalendar.model.CalendarItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageCalendarViewModel @Inject constructor(
    private val calendarLocalDataSource: CalendarLocalDataSource
) : ViewModel() {

    val calendarItemList = calendarLocalDataSource.fetchCustomCalendar()
        .map { calendarList ->
            calendarList.map { calendar -> CalendarItem.from(calendar) }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _deleteCalendarEvent = MutableSharedFlow<Unit>()
    val deleteCalendarEvent: SharedFlow<Unit> = _deleteCalendarEvent

    fun deleteCalendarItem(currentCalendarItemList: List<CalendarItem>) {
        viewModelScope.launch {
            currentCalendarItemList
                .filter { calendarItem -> calendarItem.check }
                .forEach { calendarItem ->
                    calendarLocalDataSource.deleteCalendar(
                        calendarItem.toCalendar()
                    )
                }
        }
    }

    fun emitDeleteCalendarEvent() {
        viewModelScope.launch {
            _deleteCalendarEvent.emit(Unit)
        }
    }
}
