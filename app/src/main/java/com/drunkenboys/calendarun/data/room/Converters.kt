package com.drunkenboys.calendarun.data.room

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class Converters {

    // EpochDay : 1970-01-01 이후의 시간을 Long으로 변환
    // EpochDay : 1970-01-01T00:00:00Z 이후의 시간을 Long으로 변환

    @TypeConverter
    fun fromDatestamp(value: Long): LocalDate = LocalDate.ofEpochDay(value)

    @TypeConverter
    fun dateToTimestamp(date: LocalDate): Long = date.toEpochDay()

    @TypeConverter
    fun fromDateTimestamp(value: Long): LocalDateTime = LocalDateTime.ofEpochSecond(value, 0, zoneOffset)

    @TypeConverter
    fun dateToDateTimestamp(time: LocalDateTime): Long = time.toEpochSecond(zoneOffset)

    companion object {
        private val zoneOffset = ZoneId.systemDefault().rules.getOffset(Instant.now())
    }
}
