package com.drunkenboys.calendarun.util

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
