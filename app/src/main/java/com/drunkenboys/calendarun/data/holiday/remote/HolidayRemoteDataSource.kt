package com.drunkenboys.calendarun.data.holiday.remote

import com.drunkenboys.calendarun.data.holiday.ResponseHolidayInfo
import com.drunkenboys.calendarun.data.holiday.ResponseHolidayListInfo

interface HolidayRemoteDataSource {

    suspend fun fetchHolidayListOnYear(year: String): ResponseHolidayListInfo

    suspend fun fetchHolidayListOnMonth(year: String, month: String): ResponseHolidayListInfo

    suspend fun fetchHolidayOnYear(year: String): ResponseHolidayInfo

    suspend fun fetchHolidayOnMonth(year: String, month: String): ResponseHolidayInfo

}
