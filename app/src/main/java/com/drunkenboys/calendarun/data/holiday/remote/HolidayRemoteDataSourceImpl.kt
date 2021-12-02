package com.drunkenboys.calendarun.data.holiday.remote

import com.drunkenboys.calendarun.data.holiday.ResponseHolidayInfo
import javax.inject.Inject

class HolidayRemoteDataSourceImpl @Inject constructor(
    private val holidayRemoteService: HolidayRemoteService
) : HolidayRemoteDataSource {

    override suspend fun fetchHolidayListOnYear(year: String, pageNo: Int) =
        holidayRemoteService.fetchHolidayListOnYear(year, pageNo)

    override suspend fun fetchHolidayListOnMonth(year: String, month: String) =
        holidayRemoteService.fetchHolidayListOnMonth(year, month)

    override suspend fun fetchHolidayOnYear(year: String, pageNo: Int): ResponseHolidayInfo =
        holidayRemoteService.fetchHolidayOnYear(year, pageNo)

    override suspend fun fetchHolidayOnMonth(year: String, month: String): ResponseHolidayInfo =
        holidayRemoteService.fetchHolidayOnMonth(year, month)

}
