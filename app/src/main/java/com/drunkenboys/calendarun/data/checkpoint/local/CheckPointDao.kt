package com.drunkenboys.calendarun.data.checkpoint.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drunkenboys.calendarun.data.checkpoint.entity.CheckPoint

@Dao
interface CheckPointDao {

    @Insert
    suspend fun insertCheckPoint(checkPoint: CheckPoint): Long

    @Query("SELECT * FROM `checkpoint`")
    suspend fun fetchAllCheckPoint(): List<CheckPoint>

    @Query("SELECT * FROM `checkpoint` WHERE id == :id")
    suspend fun fetchCheckPoint(id: Long): CheckPoint

    @Query("SELECT * FROM `checkpoint` WHERE calendarId == :calendarId")
    suspend fun fetchCalendarCheckPoints(calendarId: Long): List<CheckPoint>

    @Update
    suspend fun updateCheckPoint(checkPoint: CheckPoint)

    @Query("DELETE FROM `checkpoint` WHERE calendarId == :calendarId")
    suspend fun deleteCheckPointList(calendarId: Long)

}
