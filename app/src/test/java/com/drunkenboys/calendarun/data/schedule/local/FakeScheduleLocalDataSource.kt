package com.drunkenboys.calendarun.data.schedule.local

import com.drunkenboys.calendarun.data.schedule.entity.Schedule

class FakeScheduleLocalDataSource : ScheduleLocalDataSource {

    private val database = mutableListOf<Schedule>()

    override suspend fun insertSchedule(schedule: Schedule) {
        database.add(schedule)
    }

    override suspend fun fetchAllSchedule(): List<Schedule> {
        return database
    }

    override suspend fun fetchSchedule(id: Int): Schedule {
        return database.find { it.id == id } ?: throw IllegalArgumentException()
    }

    override suspend fun updateSchedule(schedule: Schedule) {
        val targetIndex = database.indexOfFirst { it.id == schedule.id }
        database.removeAt(targetIndex)
        database.add(targetIndex, schedule)
    }

    override suspend fun deleteSchedule(schedule: Schedule) {
        database.remove(schedule)
    }
}
