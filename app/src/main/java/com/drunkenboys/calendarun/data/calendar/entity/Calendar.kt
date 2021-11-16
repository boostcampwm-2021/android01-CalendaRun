package com.drunkenboys.calendarun.data.calendar.entity

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "Calendar")
data class Calendar(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    companion object {
        val diffUtil by lazy {
            object : DiffUtil.ItemCallback<Calendar>() {
                override fun areItemsTheSame(oldItem: Calendar, newItem: Calendar) = oldItem.name == newItem.name

                override fun areContentsTheSame(oldItem: Calendar, newItem: Calendar) = oldItem == newItem
            }
        }
    }
}
