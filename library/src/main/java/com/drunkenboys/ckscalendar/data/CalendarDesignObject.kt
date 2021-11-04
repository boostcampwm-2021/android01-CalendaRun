package com.drunkenboys.ckscalendar.data

// CalendarView의 디자인을 위한 설정
data class CalendarDesignObject(
    val weekDayTextColor: Int,
    val holidayTextColor: Int,
    val saturdayTextColor: Int,
    val sundayTextColor: Int,
    val textSize: Int,
    val textAlign: Int, // FIXME: -1, 0, 1
    val selectedFrameColor: Int,
    val backgroundColor: Int,
    val weekSimpleStringSet: List<String>,
    val weekFullStringSet: List<String>,
    val visibleScheduleCount: Int // 조절이 가능할까...?
    // TODO: frame shape
)