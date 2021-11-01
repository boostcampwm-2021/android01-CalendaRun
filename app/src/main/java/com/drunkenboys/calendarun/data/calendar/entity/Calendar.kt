package com.drunkenboys.calendarun.data.calendar.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "calendar")
data class Calendar(
    @PrimaryKey val id: Int,
    val name: String,
    val startDate: Date,
    val endDate: Date
)
