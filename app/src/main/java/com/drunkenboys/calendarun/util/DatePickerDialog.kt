package com.drunkenboys.calendarun.util

import android.app.DatePickerDialog
import android.content.Context
import androidx.fragment.app.Fragment
import com.drunkenboys.calendarun.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import kotlin.coroutines.resume

fun showDatePickerDialog(context: Context, dateSetListener: DatePickerDialog.OnDateSetListener) {
    val cal = Calendar.getInstance()
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH)
    val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)

    DatePickerDialog(context, dateSetListener, year, month, dayOfMonth).show()
}

suspend fun Fragment.pickDateInMillis() = suspendCancellableCoroutine<Long?> { cont ->
    val datePicker = MaterialDatePicker.Builder.datePicker()
        .setTitleText(getString(R.string.saveSchedule_pickDate))
        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        .build()
    datePicker.apply {
        addOnPositiveButtonClickListener { timeInMillis -> cont.resume(timeInMillis) }
        addOnCancelListener { if (cont.isActive) cont.resume(null) }
        addOnDismissListener { if (cont.isActive) cont.resume(null) }
        addOnNegativeButtonClickListener { if (cont.isActive) cont.resume(null) }
    }

    datePicker.show(parentFragmentManager, datePicker::class.simpleName)
}

suspend fun Fragment.pickTime() = suspendCancellableCoroutine<Pair<Int, Int>?> { cont ->
    val timePicker = MaterialTimePicker.Builder()
        .setTimeFormat(TimeFormat.CLOCK_12H)
        .setHour(12)
        .setMinute(0)
        .setTitleText(R.string.saveSchedule_pickTime)
        .build()
    timePicker.apply {
        addOnPositiveButtonClickListener { cont.resume(timePicker.hour to timePicker.minute) }
        addOnCancelListener { if (cont.isActive) cont.resume(null) }
        addOnDismissListener { if (cont.isActive) cont.resume(null) }
        addOnNegativeButtonClickListener { if (cont.isActive) cont.resume(null) }
    }

    timePicker.show(parentFragmentManager, timePicker::class.simpleName)
}
