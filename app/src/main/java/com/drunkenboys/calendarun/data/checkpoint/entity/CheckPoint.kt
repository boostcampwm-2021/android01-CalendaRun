package com.drunkenboys.calendarun.data.checkpoint.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import java.time.LocalDate

@Entity(
    tableName = "Checkpoint",
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
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val calendarId: Long,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate
)

