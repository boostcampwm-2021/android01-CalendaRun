package com.drunkenboys.calendarun.data.slice.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import java.time.LocalDate

@Entity(
    tableName = "Slice",
    foreignKeys = [
        ForeignKey(
            entity = Calendar::class,
            parentColumns = ["id"],
            childColumns = ["calendarId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class Slice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val calendarId: Long,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate
)
