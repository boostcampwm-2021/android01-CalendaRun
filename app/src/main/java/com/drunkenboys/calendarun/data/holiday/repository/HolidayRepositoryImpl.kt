package com.drunkenboys.calendarun.data.holiday.repository

import com.drunkenboys.calendarun.data.holiday.ResponseHolidayInfo
import com.drunkenboys.calendarun.data.holiday.ResponseHolidayListInfo
import com.drunkenboys.calendarun.data.holiday.entity.Holiday
import com.drunkenboys.calendarun.data.holiday.local.HolidayLocalDataSource
import com.drunkenboys.calendarun.data.holiday.remote.HolidayRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HolidayRepositoryImpl @Inject constructor(
    private val holidayLocalDataSource: HolidayLocalDataSource,
    private val holidayRemoteDataSource: HolidayRemoteDataSource
) : HolidayRepository {

    override suspend fun insertHoliday(holiday: Holiday) {
        holidayLocalDataSource.insertHoliday(holiday)
    }

    override fun fetchAllHoliday(): Flow<List<Holiday>> =
        holidayLocalDataSource.fetchAllHoliday()

    override suspend fun fetchHolidayListOnYear(year: String, pageNo: Int): ResponseHolidayListInfo =
        holidayRemoteDataSource.fetchHolidayListOnYear(year, pageNo)

    override suspend fun fetchHolidayListOnMonth(year: String, month: String): ResponseHolidayListInfo =
        holidayRemoteDataSource.fetchHolidayListOnMonth(year, month)

    override suspend fun fetchHolidayOnYear(year: String, pageNo: Int): ResponseHolidayInfo =
        holidayRemoteDataSource.fetchHolidayOnYear(year, pageNo)

    override suspend fun fetchHolidayOnMonth(year: String, month: String): ResponseHolidayInfo =
        holidayRemoteDataSource.fetchHolidayOnMonth(year, month)

}
