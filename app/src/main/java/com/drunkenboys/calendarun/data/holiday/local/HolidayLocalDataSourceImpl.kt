package com.drunkenboys.calendarun.data.holiday.local

import com.drunkenboys.calendarun.data.holiday.entity.Holiday
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HolidayLocalDataSourceImpl @Inject constructor(
    private val holidayDao: HolidayDao,
    private val dispatcher: CoroutineDispatcher
) : HolidayLocalDataSource {

    override suspend fun insertHoliday(holiday: Holiday) = withContext(dispatcher) {
        holidayDao.insertHoliday(holiday)
    }

    override fun fetchAllHoliday(): Flow<List<Holiday>> = holidayDao.fetchAllHoliday()
}
