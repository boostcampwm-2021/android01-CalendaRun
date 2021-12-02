package com.drunkenboys.ckscalendar.data

import android.graphics.Color
import com.drunkenboys.ckscalendar.utils.TimeUtils
import java.time.LocalDateTime

// CalendarView에 스케쥴 등을 표시하기 위한 오브젝트
// TODO:
data class CalendarScheduleObject(
    val id: Int,
    val color: Int = TimeUtils.getColorInt(255,245,246),
    val text: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val isHoliday: Boolean = false
)
