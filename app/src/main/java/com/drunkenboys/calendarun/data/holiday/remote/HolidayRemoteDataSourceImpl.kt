package com.drunkenboys.calendarun.data.holiday.remote

import com.drunkenboys.calendarun.data.holiday.ResponseHolidayInfo
import javax.inject.Inject

class HolidayRemoteDataSourceImpl @Inject constructor(
    private val holidayRemoteService: HolidayRemoteService
) : HolidayRemoteDataSource {

    override suspend fun fetchHolidayOnMonth(year: Int, month: Int): ResponseHolidayInfo =
        holidayRemoteService.fetchHolidayOnMonth(year, month)
}
