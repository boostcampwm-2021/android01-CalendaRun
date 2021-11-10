package com.drunkenboys.calendarun.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

val defaultZoneOffset = ZoneOffset.systemDefault().rules.getOffset(Instant.now())

fun localDateToString(date: LocalDate): String = date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

fun stringToLocalDate(date: String): LocalDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy.MM.dd"))

fun Long.toDefaultLocalDateTime(): LocalDateTime = LocalDateTime.ofEpochSecond(this / 1000, 0, defaultZoneOffset)
