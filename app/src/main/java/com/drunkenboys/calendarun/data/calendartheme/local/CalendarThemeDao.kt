package com.drunkenboys.calendarun.data.calendartheme.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.drunkenboys.calendarun.data.calendartheme.entity.CalendarTheme

@Dao
interface CalendarThemeDao {

    @Query("SELECT * FROM CalendarTheme LIMIT 1")
    suspend fun fetchCalendarTheme(): CalendarTheme

    @Update
    suspend fun updateCalendarTheme(theme: CalendarTheme)
}
