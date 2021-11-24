package com.drunkenboys.calendarun.data.calendar.local

import com.drunkenboys.calendarun.data.calendar.entity.Calendar

interface CalendarLocalDataSource {

    suspend fun insertCalendar(calendar: Calendar): Long

    suspend fun fetchAllCalendar(): List<Calendar>

    suspend fun fetchCustomCalendar(): List<Calendar>

    suspend fun fetchCalendar(id: Long): Calendar

    suspend fun deleteCalendar(calendar: Calendar)

    suspend fun updateCalendar(calendar: Calendar)

}
