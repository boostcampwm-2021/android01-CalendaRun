package com.drunkenboys.calendarun.data.holiday.remote

import com.drunkenboys.calendarun.BuildConfig
import com.drunkenboys.calendarun.data.holiday.ResponseHolidayInfo
import com.drunkenboys.calendarun.data.holiday.ResponseHolidayListInfo
import retrofit2.http.GET
import retrofit2.http.Query

interface HolidayRemoteService {

    @GET("/B090041/openapi/service/SpcdeInfoService/getRestDeInfo")
    suspend fun fetchHolidayListOnYear(
        @Query("solYear") year: String,
        @Query("pageNo") pageNo: Int,
        @Query("_type") type: String = "json",
        @Query("ServiceKey") serviceKey: String = ""
    ): ResponseHolidayListInfo

    @GET("/B090041/openapi/service/SpcdeInfoService/getRestDeInfo")
    suspend fun fetchHolidayListOnMonth(
        @Query("solYear") year: String,
        @Query("solMonth") month: String,
        @Query("_type") type: String = "json",
        @Query("ServiceKey") serviceKey: String = ""
    ): ResponseHolidayListInfo

    @GET("/B090041/openapi/service/SpcdeInfoService/getRestDeInfo")
    suspend fun fetchHolidayOnYear(
        @Query("solYear") year: String,
        @Query("pageNo") pageNo: Int,
        @Query("_type") type: String = "json",
        @Query("ServiceKey") serviceKey: String = ""
    ): ResponseHolidayInfo

    @GET("/B090041/openapi/service/SpcdeInfoService/getRestDeInfo")
    suspend fun fetchHolidayOnMonth(
        @Query("solYear") year: String,
        @Query("solMonth") month: String,
        @Query("_type") type: String = "json",
        @Query("ServiceKey") serviceKey: String = ""
    ): ResponseHolidayInfo

}
