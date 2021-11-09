package com.drunkenboys.calendarun.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun dateToString(date: Date): String {
    val dateFormat = SimpleDateFormat("yyyy.MM.dd")

    return dateFormat.format(date)
}

@SuppressLint("SimpleDateFormat")
fun stringToDate(date: String): Date {
    val dateFormat = SimpleDateFormat("yyyy.MM.dd")

    return dateFormat.parse(date)
}
