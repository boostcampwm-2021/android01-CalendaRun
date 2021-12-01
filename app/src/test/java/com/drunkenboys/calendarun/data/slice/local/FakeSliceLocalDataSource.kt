package com.drunkenboys.calendarun.data.slice.local

import com.drunkenboys.calendarun.data.slice.entity.Slice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeSliceLocalDataSource : SliceLocalDataSource {

    private val database = mutableListOf<Slice>()

    override suspend fun insertSlice(slice: Slice): Long {
        database.add(slice)
        return database.size.toLong()
    }

    override suspend fun fetchAllSlice(): List<Slice> = database

    override suspend fun fetchSlice(id: Long) = database.find { it.id == id } ?: throw IllegalArgumentException()

    override fun fetchCalendarSliceList(calendarId: Long): Flow<List<Slice>> =
        flow { emit(database.filter { it.calendarId == calendarId }) }

    override suspend fun updateSlice(slice: Slice) {
        val targetIndex = database.indexOfFirst { it.id == slice.id }
        database.removeAt(targetIndex)
        database.add(targetIndex, slice)
    }

    override suspend fun deleteSliceList(calendarId: Long) {
        database.removeAll { it.calendarId == calendarId }
    }
}
