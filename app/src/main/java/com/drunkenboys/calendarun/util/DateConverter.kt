package com.drunkenboys.calendarun.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

val defaultZoneOffset: ZoneOffset = ZoneOffset.systemDefault().rules.getOffset(Instant.now())

fun localDateToString(date: LocalDate): String = date.format(DateTimeFormatter.ofPattern("yyyy.M.d"));

fun stringToLocalDate(date: String): LocalDate =
    LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy.M.d"))
