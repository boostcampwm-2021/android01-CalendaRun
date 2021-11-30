package com.drunkenboys.calendarun.data.holiday.remote

import com.drunkenboys.calendarun.BuildConfig
import com.drunkenboys.calendarun.data.holiday.ResponseHolidayInfo
import retrofit2.http.GET
import retrofit2.http.Query

interface HolidayRemoteService {

    @GET("/B090041/openapi/service/SpcdeInfoService/getRestDeInfo")
    suspend fun fetchHolidayOnMonth(
        @Query("solYear") year: Int,
        @Query("solMonth") month: Int,
        @Query("_type") type: String = "json",
        @Query("ServiceKey") serviceKey: String = BuildConfig.HOLIDAY_API_KEY
    ): ResponseHolidayInfo

}