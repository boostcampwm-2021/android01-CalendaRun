package com.drunkenboys.ckscalendar.yearcalendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import com.drunkenboys.ckscalendar.databinding.LayoutYearCalendarBinding
import com.drunkenboys.ckscalendar.data.*
import com.drunkenboys.ckscalendar.listener.OnDayClickListener
import com.drunkenboys.ckscalendar.listener.OnDaySecondClickListener
import com.drunkenboys.ckscalendar.yearcalendar.composeView.*
import java.time.LocalDate

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
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutYearCalendarBinding by lazy { LayoutYearCalendarBinding.inflate(LayoutInflater.from(context), this, true) }

    private var onDateClickListener: OnDayClickListener? = null
    private var onDateSecondClickListener: OnDaySecondClickListener? = null

    private var viewModel = YearCalendarViewModel()

    init {
        binding.composeYearCalendarViewYearCalendar.setContent {
            //
            CustomTheme(design = viewModel.design.value) {
                // 위 -> 아래가 아닌 안 -> 밖으로 생성.
                // 요일 표시가 가장 바깥에 오지 않으면 날짜에 가려진다.
                CalendarLazyColumn(
                    onDayClickListener = onDateClickListener,
                    onDaySecondClickListener = onDateSecondClickListener,
                    viewModel = viewModel
                )
                WeekHeader(viewModel = viewModel)
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
        viewModel.setDesign(designObject)
    }

    fun resetTheme() {
        viewModel.resetDesign()
    }

    fun moveToDay(day: LocalDate) {
        // TODO: 원하는 날짜로 스크롤

        // TODO: 원하는 날짜로 클릭
    }

    fun findClickedDay(): LocalDate? {
        return viewModel.clickedDay.value?.date
    }

    fun getDaySchedules(day: LocalDate): List<CalendarScheduleObject> {
        // 해당 날짜가 포함된 스케줄 제공
        return TODO()
    }
}