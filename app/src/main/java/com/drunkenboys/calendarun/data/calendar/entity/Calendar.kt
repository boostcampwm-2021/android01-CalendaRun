package com.drunkenboys.calendarun.data.calendar.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Calendar")
data class Calendar(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
)
