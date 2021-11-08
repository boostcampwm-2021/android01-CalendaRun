package com.drunkenboys.ckscalendar.listener

import java.time.LocalDate

fun interface OnDaySecondClickListener {
    fun onDayClick(date: LocalDate, position: Int)
}
