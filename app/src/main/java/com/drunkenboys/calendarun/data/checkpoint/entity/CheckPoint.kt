package com.drunkenboys.calendarun.data.checkpoint.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
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
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    val calendarId: Int,
    val name: String,
    val startDate: Date,
    val endDate: Date,
    val color: Int
)

