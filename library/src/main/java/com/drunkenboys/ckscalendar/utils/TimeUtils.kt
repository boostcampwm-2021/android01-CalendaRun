package com.drunkenboys.ckscalendar.utils

import com.drunkenboys.ckscalendar.data.DayType
import com.drunkenboys.ckscalendar.yearcalendar.YearCalendarController
import java.time.DayOfWeek

object TimeUtils {
    fun getColorInt(R: Int, G: Int, B: Int): Int {
        return 0xff shl 24 or (R and 0xff shl 16) or (G and 0xff shl 8) or (B and 0xff)
    }

    fun parseDayWeekToDayType(week: DayOfWeek): DayType {
        return when (week) {
            DayOfWeek.SATURDAY -> DayType.SATURDAY
            DayOfWeek.SUNDAY -> DayType.SUNDAY
            else -> DayType.WEEKDAY
        }
    }

    // 일요일 시작 기준 요일값
    fun DayOfWeek.dayValue() = this.value % 7
}
