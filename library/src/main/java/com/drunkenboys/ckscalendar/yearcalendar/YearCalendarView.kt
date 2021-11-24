package com.drunkenboys.ckscalendar.yearcalendar

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.compose.foundation.layout.Column
import androidx.core.content.ContextCompat
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import com.drunkenboys.ckscalendar.R
import com.drunkenboys.ckscalendar.databinding.LayoutYearCalendarBinding
import com.drunkenboys.ckscalendar.data.*
import com.drunkenboys.ckscalendar.listener.OnDayClickListener
import com.drunkenboys.ckscalendar.listener.OnDaySecondClickListener
import com.drunkenboys.ckscalendar.yearcalendar.composeView.*
import java.time.LocalDate
import java.time.LocalDateTime

@BindingMethods(
    value = [
        BindingMethod(
            type = YearCalendarView::class,
            attribute = "app:onDayClick",
            method = "setOnDayClickListener"
        ),
        BindingMethod(
            type = YearCalendarView::class,
            attribute = "app:onDaySecondClick",
            method = "setOnDaySecondClickListener"
        )]
)
class YearCalendarView
@JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutYearCalendarBinding by lazy { LayoutYearCalendarBinding.inflate(LayoutInflater.from(context), this, true) }

    private var onDateClickListener: OnDayClickListener? = null
    private var onDateSecondClickListener: OnDaySecondClickListener? = null

    private var viewModel = YearCalendarViewModel()

    init {
        setAttr()

        binding.composeYearCalendarViewYearCalendar.setContent {
            CustomTheme(design = viewModel.design.value) {
                Column {
                    WeekHeader(viewModel = viewModel)
                    CalendarLazyColumn(
                        onDayClickListener = onDateClickListener,
                        onDaySecondClickListener = onDateSecondClickListener,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    private fun setAttr() {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.MonthCalendarView)
        val weekDayTextColor =
            attr.getColor(R.styleable.MonthCalendarView_weekDayTextColor, ContextCompat.getColor(context, R.color.calendar_black))
        val holidayTextColor = attr.getColor(R.styleable.MonthCalendarView_holidayTextColor, ScheduleColorType.RED.color)
        val saturdayTextColor = attr.getColor(R.styleable.MonthCalendarView_saturdayTextColor, ScheduleColorType.BLUE.color)
        val sundayTextColor = attr.getColor(R.styleable.MonthCalendarView_sundayTextColor, ScheduleColorType.RED.color)
        val selectedFrameColor = attr.getColor(R.styleable.MonthCalendarView_selectedFrameColor, ScheduleColorType.ORANGE.color)
        val backgroundColor =
            attr.getColor(R.styleable.MonthCalendarView_backgroundColor, ContextCompat.getColor(context, R.color.calendar_white))
        val selectedFrameDrawable =
            attr.getResourceId(R.styleable.MonthCalendarView_selectedFrameDrawable, R.drawable.bg_month_date_selected)

        attr.recycle()

        val design = when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> CalendarDesignObject.getDarkDesign()
            else -> CalendarDesignObject.getDefaultDesign()
        }

        design.apply {
            this.weekDayTextColor = weekDayTextColor
            this.holidayTextColor = holidayTextColor
            this.saturdayTextColor = saturdayTextColor
            this.sundayTextColor = sundayTextColor
            this.selectedFrameColor = selectedFrameColor
            this.backgroundColor = backgroundColor
            this.selectedFrameDrawable = selectedFrameDrawable
            viewModel.setDesign(this)
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
        viewModel.setDesign(designObject)
    }

    fun resetTheme() {
        viewModel.setDefaultDesign()
    }

    fun moveToDay(day: LocalDate) {
        // TODO: 원하는 날짜로 스크롤

        // TODO: 원하는 날짜로 클릭
    }

    fun findClickedDay(): LocalDate? = viewModel.clickedDay.value

    fun getDaySchedules(day: LocalDateTime): List<CalendarScheduleObject> = viewModel.getDaySchedules(day)

    fun setCalendarSetList(calendarSetList: List<CalendarSet>) {
        viewModel.setCalendarSetList(calendarSetList)
    }

    fun setupDefaultCalendarSet() {
        viewModel.setupDefaultCalendarSet()
    }
}