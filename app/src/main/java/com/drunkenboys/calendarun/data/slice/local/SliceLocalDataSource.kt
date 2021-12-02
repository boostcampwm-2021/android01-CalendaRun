package com.drunkenboys.calendarun.data.slice.local

import com.drunkenboys.calendarun.data.slice.entity.Slice
import kotlinx.coroutines.flow.Flow

interface SliceLocalDataSource {

    suspend fun insertSlice(slice: Slice): Long

    suspend fun fetchAllSlice(): List<Slice>

    suspend fun fetchSlice(id: Long): Slice

    fun fetchCalendarSliceList(calendarId: Long): Flow<List<Slice>>

    suspend fun updateSlice(slice: Slice)

    suspend fun deleteSliceList(calendarId: Long)

    suspend fun deleteSlice(slice: Slice)

}
