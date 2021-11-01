package com.drunkenboys.calendarun.data.schedule.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.drunkenboys.calendarun.data.schedule.entity.Schedule

@Dao
interface ScheduleDao {

    @Insert
    suspend fun insertSchedule(schedule: Schedule)

    @Query("SELECT * FROM `schedule`")
    suspend fun fetchAllSchedule()

    @Query("SELECT * FROM `schedule` WHERE id == :id")
    suspend fun fetchSchedule(id: Int)

}
