package com.drunkenboys.calendarun.data.holiday.remote

import com.drunkenboys.calendarun.data.holiday.ResponseHolidayInfo

interface HolidayDataSource {

    suspend fun fetchHolidayOnMonth(year: Int, month: Int): ResponseHolidayInfo

}
