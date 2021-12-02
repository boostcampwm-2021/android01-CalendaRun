package com.drunkenboys.calendarun.data.holiday.local

import com.drunkenboys.calendarun.data.holiday.entity.Holiday
import kotlinx.coroutines.flow.Flow

interface HolidayLocalDataSource {

    suspend fun insertHoliday(holiday: Holiday)

    fun fetchAllHoliday(): Flow<List<Holiday>>
    
}
