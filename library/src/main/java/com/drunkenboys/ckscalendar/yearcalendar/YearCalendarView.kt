package com.drunkenboys.ckscalendar.yearcalendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.drunkenboys.ckscalendar.R
import com.drunkenboys.ckscalendar.databinding.LayoutYearCalendarBinding
import com.drunkenboys.ckscalendar.FakeFactory
import java.time.DayOfWeek
import java.util.*

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

        binding.composeYearCalendarViewYearCalendar.setContent {
            YearCalendar()
        }
    }

    @Composable
    private fun YearCalendar() {

        ConstraintLayout(dayOfWeekConstraints()) {
            DayOfWeekTextView(dayOfWeek = context.getString(R.string.sunday))
            DayOfWeekTextView(dayOfWeek = context.getString(R.string.monday))
            DayOfWeekTextView(dayOfWeek = context.getString(R.string.tuesday))
            DayOfWeekTextView(dayOfWeek = context.getString(R.string.wednesday))
            DayOfWeekTextView(dayOfWeek = context.getString(R.string.thursday))
            DayOfWeekTextView(dayOfWeek = context.getString(R.string.friday))
            DayOfWeekTextView(dayOfWeek = context.getString(R.string.saturday))
        }
    }

    @Composable
    private fun DayOfWeekTextView(dayOfWeek: String) {
        Text(
            text = dayOfWeek,
            modifier = Modifier.layoutId(dayOfWeek)
        )
    }

    private fun dayOfWeekConstraints() = ConstraintSet {
        val sunday = createRefFor(context.getString(R.string.sunday))
        val monday = createRefFor(context.getString(R.string.monday))
        val tuesday = createRefFor(context.getString(R.string.tuesday))
        val wednesday = createRefFor(context.getString(R.string.wednesday))
        val thursday = createRefFor(context.getString(R.string.thursday))
        val friday = createRefFor(context.getString(R.string.friday))
        val saturday = createRefFor(context.getString(R.string.saturday))

        constrain(sunday) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(monday.start)
        }

        constrain(monday) {
            top.linkTo(parent.top)
            start.linkTo(sunday.end)
            end.linkTo(tuesday.start)
        }

        constrain(tuesday) {
            top.linkTo(parent.top)
            start.linkTo(monday.end)
            end.linkTo(wednesday.start)
        }

        constrain(wednesday) {
            top.linkTo(parent.top)
            start.linkTo(tuesday.end)
            end.linkTo(thursday.start)
        }

        constrain(thursday) {
            top.linkTo(parent.top)
            start.linkTo(wednesday.end)
            end.linkTo(friday.start)
        }

        constrain(friday) {
            top.linkTo(parent.top)
            start.linkTo(thursday.end)
            end.linkTo(saturday.start)
        }

        constrain(saturday) {
            top.linkTo(parent.top)
            start.linkTo(friday.end)
            end.linkTo(parent.end)
        }
    }
}