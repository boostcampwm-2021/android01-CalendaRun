package com.drunkenboys.calendarun.data.holiday.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.drunkenboys.calendarun.data.holiday.entity.Holiday
import kotlinx.coroutines.flow.Flow

@Dao
interface HolidayDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertHoliday(holiday: Holiday)

    @Query("SELECT * FROM `holiday`")
    fun fetchAllHoliday(): Flow<List<Holiday>>

}
