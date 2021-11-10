package com.drunkenboys.calendarun.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

fun localDateToString(date: LocalDate): String = date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

fun stringToLocalDate(date: String): LocalDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy.MM.dd"))

fun dateToLocalDateTime(date: Date): LocalDateTime = date.toInstant()
    .atZone(ZoneId.systemDefault())
    .toLocalDateTime()

fun localDateTimeToDate(localDateTime: LocalDateTime): Date = Date.from(localDateTime.toInstant(ZoneOffset.UTC))

fun dateToLocalDate(date: Date): LocalDate = date.toInstant()
    .atZone(ZoneId.systemDefault())
    .toLocalDate()

fun localDateToDate(localDate: LocalDate): Date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
