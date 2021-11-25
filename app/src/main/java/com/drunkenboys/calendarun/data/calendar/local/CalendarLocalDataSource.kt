package com.drunkenboys.calendarun.data.calendar.local

import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import kotlinx.coroutines.flow.Flow

interface CalendarLocalDataSource {

    suspend fun insertCalendar(calendar: Calendar): Long

    fun fetchAllCalendar(): Flow<List<Calendar>>

    suspend fun fetchCustomCalendar(): List<Calendar>

    suspend fun fetchCalendar(id: Long): Calendar

    suspend fun deleteCalendar(calendar: Calendar)

    suspend fun updateCalendar(calendar: Calendar)

}
