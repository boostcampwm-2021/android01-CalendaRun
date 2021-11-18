package com.drunkenboys.ckscalendar.month

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import androidx.viewpager2.widget.ViewPager2
import com.drunkenboys.ckscalendar.R
import com.drunkenboys.ckscalendar.data.CalendarDesignObject
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import com.drunkenboys.ckscalendar.data.CalendarSet
import com.drunkenboys.ckscalendar.data.ScheduleColorType
import com.drunkenboys.ckscalendar.databinding.LayoutMonthCalendarBinding
import com.drunkenboys.ckscalendar.listener.OnDayClickListener
import com.drunkenboys.ckscalendar.listener.OnDaySecondClickListener
import java.time.LocalDate

@BindingMethods(
    value = [
        BindingMethod(
            type = MonthCalendarView::class,
            attribute = "app:onDayClick",
            method = "setOnDayClickListener"
        ),
        BindingMethod(
            type = MonthCalendarView::class,
            attribute = "app:onDaySecondClick",
            method = "setOnDaySecondClickListener"
        )]
)
class MonthCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutMonthCalendarBinding by lazy { LayoutMonthCalendarBinding.inflate(LayoutInflater.from(context), this, true) }

    private val pageAdapter = MonthPageAdapter()

    private var calendarList = listOf<CalendarSet>()

    private val today = LocalDate.now()

    private val onPageChange = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        @SuppressLint("SetTextI18n")
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            pageAdapter.getCalendarSetName(position)?.let {
                binding.tvMonthCalendarViewCurrentMonth.text = it.name
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
        }
    }

    init {
        binding.vpMonthPage.adapter = pageAdapter
        binding.vpMonthPage.registerOnPageChangeCallback(onPageChange)

        setupDefaultCalendarSet()

        // xml에서 넘어온 attribute 값 적용
        val attr = context.obtainStyledAttributes(attrs, R.styleable.MonthCalendarView)
        val weekDayTextColor = attr.getColor(R.styleable.MonthCalendarView_weekDayTextColor, Color.BLACK)
        val holidayTextColor = attr.getColor(R.styleable.MonthCalendarView_holidayTextColor, ScheduleColorType.RED.color)
        val saturdayTextColor = attr.getColor(R.styleable.MonthCalendarView_saturdayTextColor, ScheduleColorType.BLUE.color)
        val sundayTextColor = attr.getColor(R.styleable.MonthCalendarView_sundayTextColor, ScheduleColorType.RED.color)
        val selectedFrameColor = attr.getColor(R.styleable.MonthCalendarView_selectedFrameColor, ScheduleColorType.ORANGE.color)
        val backgroundColor = attr.getColor(R.styleable.MonthCalendarView_backgroundColor, Color.WHITE)
        val selectedFrameDrawable =
            attr.getResourceId(R.styleable.MonthCalendarView_selectedFrameDrawable, R.drawable.bg_month_date_selected)
        attr.recycle()

        CalendarDesignObject.getDefaultDesign().apply {
            this.weekDayTextColor = weekDayTextColor
            this.holidayTextColor = holidayTextColor
            this.saturdayTextColor = saturdayTextColor
            this.sundayTextColor = sundayTextColor
            this.selectedFrameColor = selectedFrameColor
            this.backgroundColor = backgroundColor
            this.selectedFrameDrawable = selectedFrameDrawable
            setDesign(this)
        }
    }

    fun setCalendarSetList(calendarList: List<CalendarSet>) {
        this.calendarList = calendarList
        pageAdapter.setItems(calendarList, false)
        binding.tvMonthCalendarViewCurrentMonth.text = calendarList.first().name
    }

    fun setupDefaultCalendarSet() {
        calendarList = CalendarSet.generateCalendarOfYear(context, today.year)
        pageAdapter.setItems(calendarList, true)

        calendarList.forEachIndexed { index, calendarSet -> //오늘 날짜로 이동
            if (calendarSet.startDate.monthValue == today.monthValue) {
                binding.tvMonthCalendarViewCurrentMonth.text = calendarSet.name
                binding.vpMonthPage.setCurrentItem(Int.MAX_VALUE / 2 + index, false)
                return@forEachIndexed
            }
        }
    }

    fun setOnDayClickListener(onDayClickListener: OnDayClickListener) {
        pageAdapter.onDayClickListener = onDayClickListener
    }

    fun setOnDaySecondClickListener(onDaySecondClickListener: OnDaySecondClickListener) {
        pageAdapter.onDaySecondClickListener = onDaySecondClickListener
    }

    fun setSchedules(schedules: List<CalendarScheduleObject>) {
        pageAdapter.setSchedule(schedules.sortedBy { it.startDate })
    }

    fun setDesign(calendarDesign: CalendarDesignObject) {
        calendarDesign.weekDayTextColor.let {
            binding.tvMonthCalendarViewCurrentMonth.setTextColor(it)
            binding.tvMonthCalendarViewSunday.setTextColor(it)
            binding.tvMonthCalendarViewMonday.setTextColor(it)
            binding.tvMonthCalendarViewThursday.setTextColor(it)
            binding.tvMonthCalendarViewWednesday.setTextColor(it)
            binding.tvMonthCalendarViewTuesday.setTextColor(it)
            binding.tvMonthCalendarViewFriday.setTextColor(it)
            binding.tvMonthCalendarViewSaturday.setTextColor(it)
        }
        binding.root.setBackgroundColor(calendarDesign.backgroundColor)
        pageAdapter.setDesign(calendarDesign)
    }
}
