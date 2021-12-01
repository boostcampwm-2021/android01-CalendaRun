package com.drunkenboys.calendarun.data.holiday.repository

import com.drunkenboys.calendarun.data.holiday.ResponseHolidayInfo
import com.drunkenboys.calendarun.data.holiday.ResponseHolidayListInfo
import com.drunkenboys.calendarun.data.holiday.entity.Holiday
import kotlinx.coroutines.flow.Flow

interface HolidayRepository {

    suspend fun insertHoliday(holiday: Holiday)

    fun fetchAllHoliday(): Flow<List<Holiday>>

    suspend fun fetchHolidayListOnYear(year: String): ResponseHolidayListInfo

    suspend fun fetchHolidayListOnMonth(year: String, month: String): ResponseHolidayListInfo

    suspend fun fetchHolidayOnYear(year: String): ResponseHolidayInfo

    suspend fun fetchHolidayOnMonth(year: String, month: String): ResponseHolidayInfo

}
