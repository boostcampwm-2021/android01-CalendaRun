package com.drunkenboys.calendarun.data.calendar.local

import com.drunkenboys.calendarun.data.calendar.entity.Calendar

interface CalendarLocalDataSource {

    suspend fun insertCalendar(calendar: Calendar)

    suspend fun fetchAllCalendar(): List<Calendar>

    suspend fun fetchCalendar(id: Int): Calendar

}
