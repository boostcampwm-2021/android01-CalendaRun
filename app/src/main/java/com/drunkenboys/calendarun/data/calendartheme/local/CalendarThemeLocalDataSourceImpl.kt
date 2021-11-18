package com.drunkenboys.calendarun.data.calendartheme.local

import com.drunkenboys.calendarun.data.calendartheme.entity.CalendarTheme
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CalendarThemeLocalDataSourceImpl @Inject constructor(
    private val calendarThemeDao: CalendarThemeDao,
    private val dispatcher: CoroutineDispatcher
) : CalendarThemeLocalDataSource {

    override suspend fun fetchCalendarTheme(): CalendarTheme = withContext(dispatcher) {
        calendarThemeDao.fetchCalendarTheme()
    }

    override suspend fun updateCalendarTheme(theme: CalendarTheme) = withContext(dispatcher) {
        calendarThemeDao.updateCalendarTheme(theme)
    }
}
