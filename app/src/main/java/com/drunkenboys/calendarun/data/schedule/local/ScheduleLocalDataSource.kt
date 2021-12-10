package com.drunkenboys.calendarun.data.schedule.local

import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface ScheduleLocalDataSource {

    suspend fun insertSchedule(schedule: Schedule): Long

    suspend fun fetchAllSchedule(): List<Schedule>

    suspend fun fetchSchedule(id: Long): Schedule

    fun fetchCalendarSchedules(calendarId: Long): Flow<List<Schedule>>

    suspend fun updateSchedule(schedule: Schedule)

    suspend fun deleteSchedule(schedule: Schedule)

    suspend fun fetchMatchedScheduleAfter(word: String, time: Long): List<Schedule>

    suspend fun fetchMatchedScheduleBefore(word: String, time: Long): List<Schedule>

    fun fetchDateSchedule(startOfDate: LocalDateTime, endOfDate: LocalDateTime): Flow<List<Schedule>>

}
