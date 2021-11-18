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
    visibleScheduleCount = visibleScheduleCount
)
