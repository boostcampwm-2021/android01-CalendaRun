package com.drunkenboys.calendarun.data.holiday.remote

import com.drunkenboys.calendarun.data.holiday.ResponseHolidayInfo

interface HolidayRemoteDataSource {

    suspend fun fetchHolidayOnMonth(year: Int, month: Int): ResponseHolidayInfo

}
