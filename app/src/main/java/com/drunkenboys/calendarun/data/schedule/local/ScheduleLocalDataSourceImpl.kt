package com.drunkenboys.calendarun.data.schedule.local

import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ScheduleLocalDataSourceImpl @Inject constructor(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val scheduleDao: ScheduleDao
) : ScheduleLocalDataSource {

    override suspend fun insertSchedule(schedule: Schedule) = withContext(dispatcher) {
        scheduleDao.insertSchedule(schedule)
    }

    override suspend fun fetchAllSchedule() = withContext(dispatcher) {
        scheduleDao.fetchAllSchedule()
    }

    override suspend fun fetchCalendarSchedules(calendarId: Long) = withContext(dispatcher) {
        scheduleDao.fetchCalendarSchedules(calendarId)
    }

    override suspend fun fetchSchedule(id: Long) = withContext(dispatcher) {
        scheduleDao.fetchSchedule(id)
    }

    override suspend fun updateSchedule(schedule: Schedule) = withContext(dispatcher) {
        scheduleDao.updateSchedule(schedule)
    }

    override suspend fun deleteSchedule(schedule: Schedule) = withContext(dispatcher) {
        scheduleDao.deleteSchedule(schedule)
    }
}
