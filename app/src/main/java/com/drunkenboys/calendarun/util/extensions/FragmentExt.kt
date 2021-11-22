package com.drunkenboys.calendarun.util.extensions

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.util.toSecondLong
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalDate
import java.time.LocalTime
import kotlin.coroutines.resume

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

suspend fun Fragment.pickDateInMillis(localDate: LocalDate? = null) = suspendCancellableCoroutine<Long?> { cont ->
    val datePicker = MaterialDatePicker.Builder.datePicker()
        .setTitleText(getString(R.string.saveSchedule_pickDate))
        .setSelection(localDate?.toSecondLong() ?: MaterialDatePicker.todayInUtcMilliseconds())
        .build()
    datePicker.apply {
        addOnPositiveButtonClickListener { timeInMillis -> cont.resume(timeInMillis) }
        addOnCancelListener { if (cont.isActive) cont.resume(null) }
        addOnDismissListener { if (cont.isActive) cont.resume(null) }
        addOnNegativeButtonClickListener { if (cont.isActive) cont.resume(null) }
    }

    datePicker.show(parentFragmentManager, datePicker::class.simpleName)
}

suspend fun Fragment.pickTime(localTime: LocalTime? = null) = suspendCancellableCoroutine<Pair<Int, Int>?> { cont ->
    val timePicker = MaterialTimePicker.Builder()
        .setTimeFormat(TimeFormat.CLOCK_12H)
        .setHour(localTime?.hour ?: 12)
        .setMinute(localTime?.minute ?: 0)
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

// from iosched(https://github.com/google/iosched)
inline fun Fragment.launchAndRepeatWithViewLifecycle(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(state) {
            block()
        }
    }
}
