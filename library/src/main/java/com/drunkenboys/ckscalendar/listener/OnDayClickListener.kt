package com.drunkenboys.ckscalendar.listener

import java.time.LocalDate

fun interface OnDayClickListener {

    fun onDayClick(date: LocalDate, position: Int)

}
