package com.drunkenboys.calendarun.data.schedule.local

import com.drunkenboys.calendarun.data.schedule.entity.Schedule

interface ScheduleLocalDataSource {

    suspend fun insertSchedule(schedule: Schedule): Long

    suspend fun fetchAllSchedule(): List<Schedule>

    suspend fun fetchSchedule(id: Long): Schedule

    suspend fun fetchCalendarSchedules(calendarId: Long): List<Schedule>

    suspend fun updateSchedule(schedule: Schedule)

    suspend fun deleteSchedule(schedule: Schedule)

    suspend fun fetchMatchedScheduleAfter(word: String, time: Long): List<Schedule>

    suspend fun fetchMatchedScheduleBefore(word: String, time: Long): List<Schedule>

}
