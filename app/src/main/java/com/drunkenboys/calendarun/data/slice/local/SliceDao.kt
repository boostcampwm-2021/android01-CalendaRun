package com.drunkenboys.calendarun.data.slice.local

import androidx.room.*
import com.drunkenboys.calendarun.data.slice.entity.Slice
import kotlinx.coroutines.flow.Flow

@Dao
interface SliceDao {

    @Insert
    suspend fun insertSlice(slice: Slice): Long

    @Query("SELECT * FROM `Slice`")
    suspend fun fetchAllSlice(): List<Slice>

    @Query("SELECT * FROM `Slice` WHERE id == :id")
    suspend fun fetchSlice(id: Long): Slice

    @Query("SELECT * FROM `Slice` WHERE calendarId == :calendarId ORDER BY startDate ASC")
    fun fetchCalendarSliceList(calendarId: Long): Flow<List<Slice>>

    @Update
    suspend fun updateSlice(slice: Slice)

    @Query("DELETE FROM `Slice` WHERE calendarId == :calendarId")
    suspend fun deleteSliceList(calendarId: Long)

    @Delete
    suspend fun deleteSlice(slice: Slice)

    @Query("DELETE FROM `Slice` WHERE id == :id")
    suspend fun deleteSliceById(id: Long)

}
