package com.drunkenboys.calendarun.data.schedule.local

import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import java.time.LocalDate

class FakeCalendarLocalDataSource : CalendarLocalDataSource {

    override suspend fun insertCalendar(calendar: Calendar): Long {
        TODO("Not yet implemented")
    }

    override suspend fun fetchAllCalendar(): List<Calendar> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchCalendar(id: Long): Calendar {
        return Calendar(0, "test calendar", LocalDate.now(), LocalDate.now())
    }

    override suspend fun fetchCustomCalendar(): List<Calendar> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCalendar(calendar: Calendar) {
        TODO("Not yet implemented")
    }

    override suspend fun updateCalendar(calendar: Calendar) {
        TODO("Not yet implemented")
    }
}
