package com.drunkenboys.calendarun.receiver

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScheduleAlarmModel(
    val title: String,
    val content: String
) : Parcelable
