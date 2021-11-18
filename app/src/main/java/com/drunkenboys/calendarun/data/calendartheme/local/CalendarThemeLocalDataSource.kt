package com.drunkenboys.calendarun.data.calendartheme.local

import com.drunkenboys.calendarun.data.calendartheme.entity.CalendarTheme

interface CalendarThemeLocalDataSource {

    suspend fun fetchCalendarTheme(): CalendarTheme
    
    suspend fun updateCalendarTheme(theme: CalendarTheme)
}
