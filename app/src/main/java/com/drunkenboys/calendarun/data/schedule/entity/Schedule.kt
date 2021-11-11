package com.drunkenboys.calendarun.data.schedule.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.util.defaultZoneOffset
import java.time.LocalDateTime

@Entity(
    tableName = "Schedule",
    foreignKeys = [
        ForeignKey(
            entity = Calendar::class,
            parentColumns = ["id"],
            childColumns = ["calendarId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Schedule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val calendarId: Long,
    val name: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val notificationType: NotificationType,
    val memo: String,
    val color: Int
) {

    fun notificationDateTimeMillis(): Long {
        return when (notificationType) {
            NotificationType.NONE -> return 0
            NotificationType.TEN_MINUTES_AGO -> startDate.minusMinutes(10).toEpochSecond(defaultZoneOffset)
            NotificationType.A_HOUR_AGO -> startDate.minusHours(1).toEpochSecond(defaultZoneOffset)
            NotificationType.A_DAY_AGO -> startDate.minusDays(1).toEpochSecond(defaultZoneOffset)
        } * 1000
    }

    enum class NotificationType {
        NONE,
        TEN_MINUTES_AGO,
        A_HOUR_AGO,
        A_DAY_AGO
    }
}
