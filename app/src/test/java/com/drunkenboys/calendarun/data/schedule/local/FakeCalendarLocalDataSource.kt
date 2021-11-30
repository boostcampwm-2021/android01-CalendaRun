package com.drunkenboys.calendarun.data.schedule.local

import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class FakeCalendarLocalDataSource : CalendarLocalDataSource {

    override suspend fun insertCalendar(calendar: Calendar): Long {
        TODO("Not yet implemented")
    }

    override fun fetchAllCalendar(): Flow<List<Calendar>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchCalendar(id: Long): Calendar {
        return Calendar(0, "test calendar", LocalDate.now(), LocalDate.now())
    }

    override fun fetchCustomCalendar(): Flow<List<Calendar>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCalendar(calendar: Calendar) {
        TODO("Not yet implemented")
    }

    override suspend fun updateCalendar(calendar: Calendar) {
        TODO("Not yet implemented")
    }
}
