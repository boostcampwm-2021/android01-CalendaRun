package com.drunkenboys.calendarun.data.schedule.local

import androidx.room.*
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface ScheduleDao {

    @Insert
    suspend fun insertSchedule(schedule: Schedule): Long

    @Query("SELECT * FROM `schedule`")
    suspend fun fetchAllSchedule(): List<Schedule>

    @Query("SELECT * FROM `schedule` WHERE id == :id")
    suspend fun fetchSchedule(id: Long): Schedule

    @Query("SELECT * FROM `schedule` WHERE calendarId == :calendarId")
    fun fetchCalendarSchedules(calendarId: Long): Flow<List<Schedule>>

    @Update
    suspend fun updateSchedule(schedule: Schedule)

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)

    @Query("SELECT * FROM `schedule` WHERE startDate > :time AND `name` LIKE '%' || :word || '%' ORDER BY startDate ASC LIMIT $SCHEDULE_PAGING_SIZE")
    suspend fun fetchMatchedScheduleAfter(word: String, time: Long): List<Schedule>

    @Query("SELECT * FROM (SELECT * FROM `schedule` WHERE startDate < :time AND `name` LIKE '%' || :word || '%' ORDER BY startDate DESC LIMIT $SCHEDULE_PAGING_SIZE) A ORDER BY A.startDate ASC")
    suspend fun fetchMatchedScheduleBefore(word: String, time: Long): List<Schedule>

    @Query("SELECT * FROM `schedule` WHERE startDate <= :endOfDate AND endDate >= :startOfDate ORDER BY startDate ASC")
    fun fetchDateSchedule(startOfDate: LocalDateTime, endOfDate: LocalDateTime): Flow<List<Schedule>>

    companion object {

        const val SCHEDULE_PAGING_SIZE = 30
    }

}
