package com.drunkenboys.ckscalendar.yearcalendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.drunkenboys.ckscalendar.R
import com.drunkenboys.ckscalendar.databinding.LayoutYearCalendarBinding
import com.drunkenboys.ckscalendar.FakeFactory

class YearCalendarView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: LayoutYearCalendarBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.layout_year_calendar,
        this,
        true
    )

    private val yearPageMonthAdapter = YearPageAdapter()

    init {
        binding.rvYearCalendarView.adapter = yearPageMonthAdapter
        (2021..2080).forEach { year ->
            yearPageMonthAdapter.setItems(FakeFactory.createFakeCalendarSetList(year))

        }
    }
}