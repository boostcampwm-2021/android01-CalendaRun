package com.drunkenboys.calendarun.data.schedule.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import java.util.*

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
    val startDate: Date,
    val endDate: Date,
    val notificationType: NotificationType,
    val memo: String,
    val color: Int
) {

    enum class NotificationType {
        NONE,
        TEN_MINUTES_AGO,
        A_HOUR_AGO,
        A_DAY_AGO
    }
}
