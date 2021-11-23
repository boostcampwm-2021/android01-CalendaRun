package com.drunkenboys.calendarun.data.schedule.local

import androidx.room.*
import com.drunkenboys.calendarun.data.schedule.entity.Schedule

@Dao
interface ScheduleDao {

    @Insert
    suspend fun insertSchedule(schedule: Schedule): Long

    @Query("SELECT * FROM `schedule`")
    suspend fun fetchAllSchedule(): List<Schedule>

    @Query("SELECT * FROM `schedule` WHERE id == :id")
    suspend fun fetchSchedule(id: Long): Schedule

    @Query("SELECT * FROM `schedule` WHERE calendarId == :calendarId")
    suspend fun fetchCalendarSchedules(calendarId: Long): List<Schedule>

    @Update
    suspend fun updateSchedule(schedule: Schedule)

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)

    @Query("SELECT * FROM `schedule` WHERE startDate >= :time ORDER BY startDate ASC")
    suspend fun fetchScheduleAfter(time: Long): List<Schedule>

}
