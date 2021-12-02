package com.drunkenboys.calendarun.data.holiday.remote

import com.drunkenboys.calendarun.data.holiday.ResponseHolidayInfo
import javax.inject.Inject

class HolidayRemoteDataSourceImpl @Inject constructor(
    private val holidayRemoteService: HolidayRemoteService
) : HolidayRemoteDataSource {

    override suspend fun fetchHolidayListOnYear(year: String) =
        holidayRemoteService.fetchHolidayListOnYear(year)

    override suspend fun fetchHolidayListOnMonth(year: String, month: String) =
        holidayRemoteService.fetchHolidayListOnMonth(year, month)

    override suspend fun fetchHolidayOnYear(year: String): ResponseHolidayInfo =
        holidayRemoteService.fetchHolidayOnYear(year)

    override suspend fun fetchHolidayOnMonth(year: String, month: String): ResponseHolidayInfo =
        holidayRemoteService.fetchHolidayOnMonth(year, month)

}
