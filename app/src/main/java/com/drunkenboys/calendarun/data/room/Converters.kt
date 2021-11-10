package com.drunkenboys.calendarun.data.room

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class Converters {

    @TypeConverter
    fun fromDatestamp(value: Long): LocalDate = LocalDate.ofEpochDay(value)

    @TypeConverter
    fun dateToTimestamp(date: LocalDate): Long = date.toEpochDay()

    @TypeConverter
    fun fromDateTimestamp(value: Long): LocalDateTime = LocalDateTime.ofEpochSecond(value, 0, zoneOffset)

    @TypeConverter
    fun dateToDateTimestamp(time: LocalDateTime): Long = time.toEpochSecond(zoneOffset)

    companion object {
        private val zoneOffset = ZoneOffset.systemDefault().rules.getOffset(Instant.now())
    }
}
