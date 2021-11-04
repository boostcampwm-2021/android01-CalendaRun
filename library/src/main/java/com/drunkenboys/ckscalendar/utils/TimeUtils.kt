package com.drunkenboys.ckscalendar.utils

import com.drunkenboys.ckscalendar.data.DayType
import java.time.DayOfWeek

object TimeUtils {
    fun getColorInt(R: Int, G: Int, B: Int): Int {
        return 0xff shl 24 or (R and 0xff shl 16) or (G and 0xff shl 8) or (B and 0xff)
    }

//    (MonthPageAdapter.kt에서 카피)
    fun parseDayWeekToDayType(week: DayOfWeek): DayType {
        return when (week) {
            DayOfWeek.SATURDAY -> DayType.SATURDAY
            DayOfWeek.SUNDAY -> DayType.HOLIDAY
            else -> DayType.WEEKDAY
        }
    }
}