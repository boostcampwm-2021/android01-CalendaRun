package com.drunkenboys.calendarun

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun toStringDateFormat(date: Date): String {
    val dateFormat = SimpleDateFormat("yyyy.MM.dd")

    return dateFormat.format(date)
}
