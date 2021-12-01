package com.drunkenboys.calendarun.data.calendar.local

import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCalendarLocalDataSource : CalendarLocalDataSource {

    private val database = mutableListOf<Calendar>()

    override suspend fun insertCalendar(calendar: Calendar): Long {
        database.add(calendar)
        return database.size.toLong()
    }

    override fun fetchAllCalendar(): Flow<List<Calendar>> = flow { emit(database.toList()) }

    override suspend fun fetchCalendar(id: Long): Calendar = database.find { it.id == id } ?: throw IllegalArgumentException()

    override fun fetchCustomCalendar(): Flow<List<Calendar>> = flow { emit(database.filter { it.id != 1 }) }

    override suspend fun deleteCalendar(calendar: Calendar) {
        database.remove(calendar)
    }

    override suspend fun updateCalendar(calendar: Calendar) {
        val targetIndex = database.indexOfFirst { it.id == calendar.id }
        database.removeAt(targetIndex)
        database.add(targetIndex, calendar)
    }
}
