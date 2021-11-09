package com.drunkenboys.calendarun.util

import android.app.DatePickerDialog
import android.content.Context
import java.util.*

fun showDatePickerDialog(context: Context, dateSetListener: DatePickerDialog.OnDateSetListener) {
    val cal = Calendar.getInstance()
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH)
    val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)

    DatePickerDialog(context, dateSetListener, year, month, dayOfMonth).show()
}
