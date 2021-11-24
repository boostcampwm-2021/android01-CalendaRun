package com.drunkenboys.calendarun.data.checkpoint.local

import com.drunkenboys.calendarun.data.checkpoint.entity.CheckPoint

interface CheckPointLocalDataSource {

    suspend fun insertCheckPoint(checkPoint: CheckPoint): Long

    suspend fun fetchAllCheckPoint(): List<CheckPoint>

    suspend fun fetchCheckPoint(id: Long): CheckPoint

    suspend fun fetchCalendarCheckPoints(calendarId: Long): List<CheckPoint>

    suspend fun updateCheckPoint(checkPoint: CheckPoint)

}
