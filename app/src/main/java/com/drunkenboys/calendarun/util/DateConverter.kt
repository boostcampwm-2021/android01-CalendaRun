package com.drunkenboys.calendarun.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

val defaultZoneOffset: ZoneOffset = ZoneOffset.systemDefault().rules.getOffset(Instant.now())

fun stringToLocalDate(date: String): LocalDate =
    LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy.M.d"))

fun LocalDate?.localDateToString(): String = this?.format(DateTimeFormatter.ofPattern("yyyy.M.d")) ?: ""

fun LocalDate.toSecondLong() = toEpochDay() * 24 * 60 * 60 * 1000

fun LocalDate.toLong() = toEpochDay()

fun LocalDate.nextDay(): LocalDate = LocalDate.ofEpochDay(toLong() + 1L)
