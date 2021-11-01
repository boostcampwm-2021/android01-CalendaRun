package com.drunkenboys.calendarun.data.local.calendar.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
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
    @PrimaryKey val id: Int,
    val calendarId: Int,
    val name: String,
    val startDate: Date,
    val endDate: Date,
    val notification: Date,
    val memo: String,
    // TODO <Library>.ScheduleColorType
    val color: Int
)
