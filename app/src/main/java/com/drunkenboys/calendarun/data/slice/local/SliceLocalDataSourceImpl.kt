package com.drunkenboys.calendarun.data.slice.local

import com.drunkenboys.calendarun.data.slice.entity.Slice
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SliceLocalDataSourceImpl @Inject constructor(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val sliceDao: SliceDao
) : SliceLocalDataSource {

    override suspend fun insertSlice(slice: Slice) = withContext(dispatcher) {
        sliceDao.insertSlice(slice)
    }

    override suspend fun fetchAllSlice() = withContext(dispatcher) {
        sliceDao.fetchAllSlice()
    }

    override suspend fun fetchSlice(id: Long) = withContext(dispatcher) {
        sliceDao.fetchSlice(id)
    }

    override fun fetchCalendarSliceList(calendarId: Long) = sliceDao.fetchCalendarSliceList(calendarId)

    override suspend fun updateSlice(slice: Slice) {
        sliceDao.updateSlice(slice)
    }

    override suspend fun deleteSliceList(calendarId: Long) {
        sliceDao.deleteSliceList(calendarId)
    }

    override suspend fun deleteSlice(slice: Slice) {
        sliceDao.deleteSlice(slice)
    }
}
