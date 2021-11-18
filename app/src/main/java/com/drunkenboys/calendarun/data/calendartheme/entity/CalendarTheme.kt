package com.drunkenboys.calendarun.data.calendartheme.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CalendarTheme")
data class CalendarTheme(
    @PrimaryKey val id: Long = 1,
    val weekDayTextColor: Int,
    val holidayTextColor: Int,
    val saturdayTextColor: Int,
    val sundayTextColor: Int,
    val selectedFrameColor: Int,
    val backgroundColor: Int,
    val textSize: Int,
    val textAlign: Int,
    val languageType: LanguageType,
    val visibleScheduleCount: Int
) {

    enum class LanguageType(val description: String) {
        KOREAN("한글"),
        ENGLISH("영어")
    }
}
