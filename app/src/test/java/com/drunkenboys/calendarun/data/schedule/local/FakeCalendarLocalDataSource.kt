package com.drunkenboys.calendarun.data.schedule.local

import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import java.util.*

class FakeCalendarLocalDataSource : CalendarLocalDataSource {

    override suspend fun insertCalendar(calendar: Calendar) {
        TODO("Not yet implemented")
    }

    override suspend fun fetchAllCalendar(): List<Calendar> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchCalendar(id: Int): Calendar {
        return Calendar(0, "test calendar", Date(), Date())
    }
}
