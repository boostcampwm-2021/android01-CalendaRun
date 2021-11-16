package com.drunkenboys.ckscalendar.yearcalendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.drunkenboys.ckscalendar.databinding.LayoutYearCalendarBinding
import com.drunkenboys.ckscalendar.data.*
import com.drunkenboys.ckscalendar.listener.OnDayClickListener
import com.drunkenboys.ckscalendar.listener.OnDaySecondClickListener
import com.drunkenboys.ckscalendar.yearcalendar.composeView.*

class YearCalendarView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutYearCalendarBinding by lazy { LayoutYearCalendarBinding.inflate(LayoutInflater.from(context), this, true) }

    private var design = CalendarDesignObject()

    private var onDateClickListener: OnDayClickListener? = null
    private var onDateSecondClickListener: OnDaySecondClickListener? = null

    private var viewModel = YearCalendarViewModel()

    init {
        binding.composeYearCalendarViewYearCalendar.setContent {
            //
            CustomTheme(design = design) {
                // 위 -> 아래가 아닌 안 -> 밖으로 생성.
                // 요일 표시가 가장 바깥에 오지 않으면 날짜에 가려진다.
                CalendarLazyColumn(
                    design = design,
                    onDayClickListener = onDateClickListener,
                    onDaySecondClickListener = onDateSecondClickListener,
                    viewModel = viewModel
                )
                WeekHeader(design = design)
            }
        }
    }

    fun setOnDateClickListener(onDateClickListener: OnDayClickListener) {
        this.onDateClickListener = onDateClickListener
    }

    fun setOnDaySecondClickListener(onDateSecondClickListener: OnDaySecondClickListener) {
        this.onDateSecondClickListener = onDateSecondClickListener
    }

    fun setSchedule(schedule: CalendarScheduleObject) {
        viewModel.setSchedule(schedule)
    }

    fun setSchedules(schedules: List<CalendarScheduleObject>) {
        viewModel.setSchedules(schedules)
    }

    fun setTheme(designObject: CalendarDesignObject) {
        design = designObject
    }

    fun resetTheme() {
        design = CalendarDesignObject()
    }

    companion object {
        const val TAG = "YEAR_CALENDAR"
        const val INIT_YEAR = 0
        const val LAST_YEAR = 10000
    }
}