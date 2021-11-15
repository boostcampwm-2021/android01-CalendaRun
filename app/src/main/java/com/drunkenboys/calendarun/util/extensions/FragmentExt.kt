package com.drunkenboys.calendarun.util.extensions

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.drunkenboys.calendarun.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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

inline fun <T> Fragment.stateCollect(stateFlow: StateFlow<T>, crossinline block: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            stateFlow.collect {
                block(it)
            }
        }
    }
}

inline fun <T> Fragment.sharedCollect(sharedFlow: SharedFlow<T>, crossinline block: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        sharedFlow.collectLatest {
            block(it)
        }
    }
}
