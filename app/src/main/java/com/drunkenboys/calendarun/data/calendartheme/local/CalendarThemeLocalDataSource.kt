package com.drunkenboys.calendarun.data.calendartheme.local

import com.drunkenboys.calendarun.data.calendartheme.entity.CalendarTheme
import kotlinx.coroutines.flow.Flow

interface CalendarThemeLocalDataSource {

    fun fetchCalendarTheme(): Flow<CalendarTheme>

    suspend fun updateCalendarTheme(theme: CalendarTheme)
}
