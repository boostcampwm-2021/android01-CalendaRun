package com.drunkenboys.calendarun.data.checkpoint.local

import com.drunkenboys.calendarun.data.checkpoint.entity.CheckPoint
import kotlinx.coroutines.flow.Flow

interface CheckPointLocalDataSource {

    suspend fun insertCheckPoint(checkPoint: CheckPoint): Long

    suspend fun fetchAllCheckPoint(): List<CheckPoint>

    suspend fun fetchCheckPoint(id: Long): CheckPoint

    fun fetchCalendarCheckPoints(calendarId: Long): Flow<List<CheckPoint>>

    suspend fun updateCheckPoint(checkPoint: CheckPoint)

    suspend fun deleteCheckPointList(calendarId: Long)
}
