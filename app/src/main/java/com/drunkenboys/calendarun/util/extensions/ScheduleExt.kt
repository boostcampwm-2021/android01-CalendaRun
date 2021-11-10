package com.drunkenboys.calendarun.util.extensions

import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import java.util.*

fun Schedule.notificationDate(): Long {
    val calendar = Calendar.getInstance()
    calendar.time = startDate

    when (notificationType) {
        Schedule.NotificationType.NONE -> return 0
        Schedule.NotificationType.TEN_MINUTES_AGO -> calendar.add(Calendar.MINUTE, -10)
        Schedule.NotificationType.A_HOUR_AGO -> calendar.add(Calendar.HOUR_OF_DAY, -1)
        Schedule.NotificationType.A_DAY_AGO -> calendar.add(Calendar.DAY_OF_YEAR, -1)
    }

    return calendar.timeInMillis
}
