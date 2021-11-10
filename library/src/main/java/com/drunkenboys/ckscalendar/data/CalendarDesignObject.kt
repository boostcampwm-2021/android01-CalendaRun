package com.drunkenboys.ckscalendar.data

import androidx.annotation.ColorInt

data class CalendarDesignObject(
    @ColorInt val weekDayTextColor: Int,
    @ColorInt val holidayTextColor: Int,
    @ColorInt val saturdayTextColor: Int,
    @ColorInt val sundayTextColor: Int,
    val textSize: Int,
    val textAlign: Int, // TODO: gravity 매퍼
    @ColorInt val selectedFrameColor: Int,
    @ColorInt val backgroundColor: Int,
    val weekSimpleStringSet: List<String>,
    val weekFullStringSet: List<String>,
    val visibleScheduleCount: Int
    // TODO: frame shape
)