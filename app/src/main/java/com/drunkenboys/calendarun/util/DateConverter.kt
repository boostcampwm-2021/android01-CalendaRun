package com.drunkenboys.calendarun.util

import com.drunkenboys.calendarun.util.DateFormatLimitType.*
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

val defaultZoneOffset: ZoneOffset = ZoneOffset.systemDefault().rules.getOffset(Instant.now())

fun LocalDate?.localDateToString(): String = this?.format(DateTimeFormatter.ofPattern("yyyy.M.d")) ?: ""

val LocalDate.milliseconds get() = seconds * 1000

val LocalDate.seconds get() = toEpochDay() * 24 * 60 * 60

val LocalDateTime.amPm: String get() = if (hour < 12) "오전" else "오후"

fun relativeDateFormat(baseDateTime: LocalDateTime, targetDateTime: LocalDateTime, limit: DateFormatLimitType = NONE)
        : DateTimeFormatter = when {
    limit == YEAR || baseDateTime.year != targetDateTime.year -> DateTimeFormatter.ofPattern("yyyy년 M월 d일 ${targetDateTime.amPm} hh:mm")
    limit == MONTH || baseDateTime.month != targetDateTime.month -> DateTimeFormatter.ofPattern("M월 d일 ${targetDateTime.amPm} hh:mm")
    limit == DAY || baseDateTime.dayOfYear != targetDateTime.dayOfYear -> DateTimeFormatter.ofPattern("d일 ${targetDateTime.amPm} hh:mm")
    else -> DateTimeFormatter.ofPattern("${targetDateTime.amPm} hh:mm")
}

fun relativeDateFormat(baseDate: LocalDate, targetDate: LocalDate): DateTimeFormatter = when {
    baseDate.year != targetDate.year -> DateTimeFormatter.ofPattern("yyyy년 M월 d일")
    else -> DateTimeFormatter.ofPattern("M월 d일")
}

enum class DateFormatLimitType {
    YEAR,
    MONTH,
    DAY,
    NONE
}
