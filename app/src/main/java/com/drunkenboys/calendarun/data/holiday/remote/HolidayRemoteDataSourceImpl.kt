package com.drunkenboys.calendarun.data.holiday.remote

import javax.inject.Inject

class HolidayRemoteDataSourceImpl @Inject constructor(
    private val holidayRemoteService: HolidayRemoteService
) : HolidayRemoteDataSource {

    override suspend fun fetchHolidayOnYear(year: String) =
        holidayRemoteService.fetchHolidayOnYear(year)

    override suspend fun fetchHolidayOnMonth(year: String, month: String) =
        holidayRemoteService.fetchHolidayOnMonth(year, month)
}
