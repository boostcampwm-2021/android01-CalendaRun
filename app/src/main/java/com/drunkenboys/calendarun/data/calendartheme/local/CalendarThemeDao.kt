package com.drunkenboys.calendarun.data.calendartheme.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.drunkenboys.calendarun.data.calendartheme.entity.CalendarTheme
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarThemeDao {

    @Query("SELECT * FROM CalendarTheme where id = 1")
    fun fetchCalendarTheme(): Flow<CalendarTheme>

    @Update
    suspend fun updateCalendarTheme(theme: CalendarTheme)
}
