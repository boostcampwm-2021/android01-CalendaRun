package com.drunkenboys.calendarun.data.schedule.local

import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
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

    override fun fetchCalendarSchedules(calendarId: Long) = scheduleDao.fetchCalendarSchedules(calendarId)

    override suspend fun fetchSchedule(id: Long) = withContext(dispatcher) {
        scheduleDao.fetchSchedule(id)
    }

    override suspend fun updateSchedule(schedule: Schedule) = withContext(dispatcher) {
        scheduleDao.updateSchedule(schedule)
    }

    override suspend fun deleteSchedule(schedule: Schedule) = withContext(dispatcher) {
        scheduleDao.deleteSchedule(schedule)
    }

    override suspend fun fetchMatchedScheduleAfter(word: String, time: Long) = withContext(dispatcher) {
        scheduleDao.fetchMatchedScheduleAfter(word, time)
    }

    override suspend fun fetchMatchedScheduleBefore(word: String, time: Long) = withContext(dispatcher) {
        scheduleDao.fetchMatchedScheduleBefore(word, time)
    }

    override fun fetchDateSchedule(date: LocalDateTime) =
        scheduleDao.fetchDateSchedule(date)

}
