package com.drunkenboys.calendarun.data.local.calendar.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "checkpoint",
    foreignKeys = [
        ForeignKey(
            entity = Calendar::class,
            parentColumns = ["id"],
            childColumns = ["calendarId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CheckPoint(
    @PrimaryKey val id: Int,
    val calendarId: Int,
    val name: String,
    val startDate: Date,
    val endDate: Date,
    val color: Int
)

