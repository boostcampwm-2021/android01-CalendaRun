package com.drunkenboys.calendarun.util.extensions

import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.util.defaultZoneOffset

fun Schedule.notificationDateTimeMillis(): Long {
    return when (notificationType) {
        Schedule.NotificationType.NONE -> return 0
        Schedule.NotificationType.TEN_MINUTES_AGO -> startDate.minusMinutes(10).toEpochSecond(defaultZoneOffset)
        Schedule.NotificationType.A_HOUR_AGO -> startDate.minusHours(1).toEpochSecond(defaultZoneOffset)
        Schedule.NotificationType.A_DAY_AGO -> startDate.minusDays(1).toEpochSecond(defaultZoneOffset)
    } * 1000
}
