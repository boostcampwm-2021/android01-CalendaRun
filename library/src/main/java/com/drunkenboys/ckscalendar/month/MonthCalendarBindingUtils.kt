package com.drunkenboys.ckscalendar.month

import androidx.databinding.BindingAdapter
import com.drunkenboys.ckscalendar.listener.OnDayClickListener
import com.drunkenboys.ckscalendar.listener.OnDaySecondClickListener

@BindingAdapter("onDayClick")
fun onDayClickListener(view: MonthCalendarView, onDayClickListener: OnDayClickListener) {
    view.setOnDateClickListener(onDayClickListener)
}

@BindingAdapter("onDaySecondClick")
fun onDaySecondClickListener(view: MonthCalendarView, onDaySecondClickListener: OnDaySecondClickListener) {
    view.setOnDaySecondClickListener(onDaySecondClickListener)
}
