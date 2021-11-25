package com.drunkenboys.calendarun.data.calendar.local

import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CalendarLocalDataSourceImpl @Inject constructor(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val calendarDao: CalendarDao
) : CalendarLocalDataSource {

    override suspend fun insertCalendar(calendar: Calendar) = withContext(dispatcher) {
        calendarDao.insertCalendar(calendar)
    }

    override suspend fun fetchAllCalendar() = withContext(dispatcher) {
        calendarDao.fetchAllCalendar()
    }

    override suspend fun fetchCustomCalendar() = withContext(dispatcher) {
        calendarDao.fetchCustomCalendar()
    }

    override suspend fun fetchCalendar(id: Long) = withContext(dispatcher) {
        calendarDao.fetchCalendar(id)
    }

    override suspend fun deleteCalendar(calendar: Calendar) {
        calendarDao.deleteCalendar(calendar)
    }

    override suspend fun updateCalendar(calendar: Calendar) {
        calendarDao.updateCalendar(calendar)
    }
}
