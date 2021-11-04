package com.drunkenboys.calendarun

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun toStringDateFormat(date: Date): String {
    val dateFormat = SimpleDateFormat("yyyy.MM.dd")

    return dateFormat.format(date)
}

fun showDatePickerDialog(context: Context, dateSetListener: DatePickerDialog.OnDateSetListener) {
    val cal = Calendar.getInstance()
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH)
    val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)

    DatePickerDialog(context, dateSetListener, year, month, dayOfMonth).show()
}
