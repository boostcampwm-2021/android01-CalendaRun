package com.drunkenboys.calendarun.data.checkpoint.local

import com.drunkenboys.calendarun.data.checkpoint.entity.CheckPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CheckPointLocalDataSourceImpl @Inject constructor(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val checkPointDao: CheckPointDao
) : CheckPointLocalDataSource {

    override suspend fun insertCheckPoint(checkPoint: CheckPoint) = withContext(dispatcher) {
        checkPointDao.insertCheckPoint(checkPoint)
    }

    override suspend fun fetchAllCheckPoint() = withContext(dispatcher) {
        checkPointDao.fetchAllCheckPoint()
    }

    override suspend fun fetchCheckPoint(id: Long) = withContext(dispatcher) {
        checkPointDao.fetchCheckPoint(id)
    }

    override fun fetchCalendarCheckPoints(calendarId: Long) = checkPointDao.fetchCalendarCheckPoints(calendarId)

    override suspend fun updateCheckPoint(checkPoint: CheckPoint) {
        checkPointDao.updateCheckPoint(checkPoint)
    }

    override suspend fun deleteCheckPointList(calendarId: Long) {
        checkPointDao.deleteCheckPointList(calendarId)
    }
}
