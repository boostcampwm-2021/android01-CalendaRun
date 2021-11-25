package com.drunkenboys.calendarun.data.holiday.remote

import com.drunkenboys.calendarun.data.holiday.ResponseHolidayInfo
import javax.inject.Inject

class HolidayDataSourceImpl @Inject constructor(
    private val holidayService: HolidayService
) : HolidayDataSource {

    override suspend fun fetchHolidayOnMonth(year: Int, month: Int): ResponseHolidayInfo =
        holidayService.fetchHolidayOnMonth(year, month)
}
