package com.drunkenboys.calendarun.data.calendar.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.drunkenboys.calendarun.data.calendar.entity.Calendar

@Dao
interface CalendarDao {

    @Insert
    suspend fun insertCalendar(calendar: Calendar)

    @Query("SELECT * FROM `calendar`")
    suspend fun fetchAllCalendar(): List<Calendar>

    @Query("SELECT * FROM `calendar` WHERE id == :id")
    suspend fun fetchCalendar(id: Int): Calendar
    
}
