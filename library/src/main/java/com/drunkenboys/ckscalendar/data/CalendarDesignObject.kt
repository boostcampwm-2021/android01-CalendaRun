package com.drunkenboys.ckscalendar.data

import androidx.annotation.ColorInt
import com.drunkenboys.ckscalendar.utils.TimeUtils

data class CalendarDesignObject(
    @ColorInt val weekDayTextColor: Int = TimeUtils.getColorInt(0, 0, 0),
    @ColorInt val holidayTextColor: Int = ScheduleColorType.RED.color,
    @ColorInt val saturdayTextColor: Int = ScheduleColorType.BLUE.color,
    @ColorInt val sundayTextColor: Int = ScheduleColorType.RED.color,
    val textSize: Int = 10,
    val textAlign: Int = 1, // TODO: gravity 매퍼
    @ColorInt val selectedFrameColor: Int = ScheduleColorType.MAGENTA.color,
    @ColorInt val backgroundColor: Int = TimeUtils.getColorInt(255, 255, 255),
    val weekSimpleStringSet: List<String> = listOf("일", "월", "화", "수", "목", "금", "토"),
    val weekFullStringSet: List<String> = listOf("일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"),
    val visibleScheduleCount: Int = 3
    // TODO: frame shape
)
