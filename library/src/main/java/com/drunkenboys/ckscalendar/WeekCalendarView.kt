package com.drunkenboys.ckscalendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.CalendarDesignObject
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import com.drunkenboys.ckscalendar.data.DayType
import com.drunkenboys.ckscalendar.databinding.LayoutWeekCalendarBinding
import com.drunkenboys.ckscalendar.listener.OnDayClickListener
import com.drunkenboys.ckscalendar.weekcalendar.WeekCalendar
import com.drunkenboys.ckscalendar.yearcalendar.CustomTheme
import com.drunkenboys.ckscalendar.yearcalendar.YearCalendarViewModel
import java.time.LocalDate

class WeekCalendarView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutWeekCalendarBinding by lazy {
        LayoutWeekCalendarBinding.inflate(LayoutInflater.from(context), this, true)
    }

    private var onDateClickListener: OnDayClickListener? = null

    private val viewModel = YearCalendarViewModel()

    init {
        binding.composeWeek.setContent {
            val today = CalendarDate(date = LocalDate.now(), dayType = DayType.WEEKDAY)
            var clickedDay by remember { mutableStateOf<CalendarDate?>(today) }
            val clickedEdge = { day: CalendarDate ->
                BorderStroke(
                    width = 2.dp,
                    color = if (clickedDay?.date == day.date) Color(viewModel.design.value.selectedFrameColor) else Color.Transparent
                )
            }

            val dayColumnModifier = { day: CalendarDate ->
                Modifier
                    .layoutId(day.date.toString())
                    .border(clickedEdge(day))
                    .clickable(onClick = {
                        if (clickedDay != day) {
                            onDateClickListener?.onDayClick(day.date, 0)
                            clickedDay = day
                        } else clickedDay = null
                    })
            }

            CustomTheme(design = viewModel.design.value) {
                WeekCalendar(
                    viewModel = viewModel,
                    dayModifier = dayColumnModifier
                )
            }
        }
    }

    fun setOnDateClickListener(onDateClickListener: OnDayClickListener) {
        this.onDateClickListener = onDateClickListener
    }

    fun setSchedule(schedule: CalendarScheduleObject) {
        viewModel.setSchedule(schedule)
    }

    fun setSchedules(schedules: List<CalendarScheduleObject>) {
        viewModel.setSchedules(schedules)
    }

    fun setDesign(designObject: CalendarDesignObject) {
        viewModel.setDesign(designObject)
    }

    fun setDefaultDesign() {
        viewModel.setDefaultDesign()
    }
}