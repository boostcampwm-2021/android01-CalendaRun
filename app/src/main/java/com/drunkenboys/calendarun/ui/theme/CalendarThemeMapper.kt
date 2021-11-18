package com.drunkenboys.calendarun.ui.theme

import com.drunkenboys.calendarun.data.calendartheme.entity.CalendarTheme
import com.drunkenboys.ckscalendar.data.CalendarDesignObject

fun CalendarTheme.toCalendarDesignObject() = CalendarDesignObject(
    weekDayTextColor = weekDayTextColor,
    holidayTextColor = holidayTextColor,
    saturdayTextColor = saturdayTextColor,
    sundayTextColor = sundayTextColor,
    selectedFrameColor = selectedFrameColor,
    backgroundColor = backgroundColor,
    textSize = textSize,
    textAlign = textAlign,
    weekSimpleStringSet = languageType.weekSimpleStringSet,
    weekFullStringSet = languageType.weekFullStringSet,
    visibleScheduleCount = visibleScheduleCount
)

fun CalendarDesignObject.toCalendarTheme() = CalendarTheme(
    weekDayTextColor = weekDayTextColor,
    holidayTextColor = holidayTextColor,
    saturdayTextColor = saturdayTextColor,
    sundayTextColor = sundayTextColor,
    selectedFrameColor = selectedFrameColor,
    backgroundColor = backgroundColor,
    textSize = textSize,
    textAlign = textAlign,
    languageType = CalendarTheme.LanguageType.KOREAN,
    visibleScheduleCount = visibleScheduleCount
)

val CalendarTheme.LanguageType.weekSimpleStringSet: List<String>
    get() = when (this) {
        CalendarTheme.LanguageType.KOREAN -> listOf("일", "월", "화", "수", "목", "금", "토")
        CalendarTheme.LanguageType.ENGLISH -> listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    }
val CalendarTheme.LanguageType.weekFullStringSet: List<String>
    get() = when (this) {
        CalendarTheme.LanguageType.KOREAN -> listOf("일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일")
        CalendarTheme.LanguageType.ENGLISH -> listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    }
