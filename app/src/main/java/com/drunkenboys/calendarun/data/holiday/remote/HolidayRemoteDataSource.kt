package com.drunkenboys.calendarun.data.holiday.remote

import com.drunkenboys.calendarun.data.holiday.ResponseHolidayInfo

interface HolidayRemoteDataSource {

    suspend fun fetchHolidayOnYear(year: String): ResponseHolidayInfo

    suspend fun fetchHolidayOnMonth(year: String, month: String): ResponseHolidayInfo

}
