package com.drunkenboys.calendarun.data.checkpoint.local

import com.drunkenboys.calendarun.data.checkpoint.entity.CheckPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCheckPointLocalDataSource : CheckPointLocalDataSource {

    private val database = mutableListOf<CheckPoint>()

    override suspend fun insertCheckPoint(checkPoint: CheckPoint): Long {
        database.add(checkPoint)
        return database.size.toLong()
    }

    override suspend fun fetchAllCheckPoint(): List<CheckPoint> = database

    override suspend fun fetchCheckPoint(id: Long) = database.find { it.id == id } ?: throw IllegalArgumentException()

    override fun fetchCalendarCheckPoints(calendarId: Long): Flow<List<CheckPoint>> =
        flow { emit(database.filter { it.calendarId == calendarId }) }

    override suspend fun updateCheckPoint(checkPoint: CheckPoint) {
        val targetIndex = database.indexOfFirst { it.id == checkPoint.id }
        database.removeAt(targetIndex)
        database.add(targetIndex, checkPoint)
    }

    override suspend fun deleteCheckPointList(calendarId: Long) {
        database.removeAll { it.calendarId == calendarId }
    }
}
