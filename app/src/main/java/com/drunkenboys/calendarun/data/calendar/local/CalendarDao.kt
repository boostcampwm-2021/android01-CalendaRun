package com.drunkenboys.calendarun.data.calendar.local

import androidx.room.*
import com.drunkenboys.calendarun.data.calendar.entity.Calendar

@Dao
interface CalendarDao {

    @Insert
    suspend fun insertCalendar(calendar: Calendar): Long

    @Query("SELECT * FROM `calendar`")
    suspend fun fetchAllCalendar(): List<Calendar>

    @Query("SELECT * FROM `calendar` WHERE id != 1")
    suspend fun fetchCustomCalendar(): List<Calendar>

    @Query("SELECT * FROM `calendar` WHERE id == :id")
    suspend fun fetchCalendar(id: Long): Calendar

    @Delete
    suspend fun deleteCalendar(calendar: Calendar)

    @Update
    suspend fun updateCalendar(calendar: Calendar)

}
