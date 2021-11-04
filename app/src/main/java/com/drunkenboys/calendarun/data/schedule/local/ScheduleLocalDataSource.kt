package com.drunkenboys.calendarun.data.schedule.local

import com.drunkenboys.calendarun.data.schedule.entity.Schedule

interface ScheduleLocalDataSource {

    suspend fun insertSchedule(schedule: Schedule)

    suspend fun fetchAllSchedule(): List<Schedule>

    suspend fun fetchSchedule(id: Int): Schedule

    suspend fun updateSchedule(schedule: Schedule)

    suspend fun deleteSchedule(schedule: Schedule)

}
