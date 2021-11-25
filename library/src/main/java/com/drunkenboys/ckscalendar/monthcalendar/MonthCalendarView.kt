package com.drunkenboys.ckscalendar.monthcalendar

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
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

    private var hasRestore = false
    private var lastCalendarType = MonthState.CalendarType.NONE
    private var lastPagePosition = -1
    private var lastPageName = ""

    private val onPageChange = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        @SuppressLint("SetTextI18n")
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            lastPagePosition = position
            pageAdapter.getCalendarSetName(position)?.let {
                binding.tvMonthCalendarViewCurrentMonth.text = it.name
                lastPageName = it.name
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
        }
    }

    init {
        binding.vpMonthPage.adapter = pageAdapter
        binding.vpMonthPage.registerOnPageChangeCallback(onPageChange)

        // xml에서 넘어온 attribute 값 적용 ContextCompat.getColor
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
            setDesign(this)
        }
    }

    fun setCalendarSetList(calendarList: List<CalendarSet>) {
        this.calendarList = calendarList
        pageAdapter.setItems(calendarList, false)
        binding.tvMonthCalendarViewCurrentMonth.text = calendarList.first().name
        hasRestore = false
        lastCalendarType = MonthState.CalendarType.CUSTOM
    }

    fun setupDefaultCalendarSet() {
        calendarList = CalendarSet.generateCalendarOfYear(context, today.year)
        pageAdapter.setItems(calendarList, true)
        if (hasRestore && lastCalendarType == MonthState.CalendarType.DEFAULT) {
            hasRestore = false
            binding.vpMonthPage.setCurrentItem(lastPagePosition, false)
            binding.tvMonthCalendarViewCurrentMonth.text = lastPageName

        } else {
            calendarList.forEachIndexed { index, calendarSet -> //오늘 날짜로 이동
                if (calendarSet.startDate.monthValue == today.monthValue) {
                    binding.tvMonthCalendarViewCurrentMonth.text = calendarSet.name
                    binding.vpMonthPage.setCurrentItem(Int.MAX_VALUE / 2 + index, false)
                    return@forEachIndexed
                }
            }
        }
        lastCalendarType = MonthState.CalendarType.DEFAULT
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
        val weekTextViews = listOf(
            binding.tvMonthCalendarViewSunday,
            binding.tvMonthCalendarViewMonday,
            binding.tvMonthCalendarViewThursday,
            binding.tvMonthCalendarViewWednesday,
            binding.tvMonthCalendarViewTuesday,
            binding.tvMonthCalendarViewFriday,
            binding.tvMonthCalendarViewSaturday,
        )

        binding.tvMonthCalendarViewCurrentMonth.setTextColor(calendarDesign.weekDayTextColor)
        weekTextViews.forEachIndexed { index, textView ->
            textView.setTextColor(calendarDesign.weekDayTextColor)
            textView.text = calendarDesign.weekSimpleStringSet[index]
        }

        binding.root.setBackgroundColor(calendarDesign.backgroundColor)
        pageAdapter.setDesign(calendarDesign)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val parcelable = super.onSaveInstanceState() ?: return null
        return MonthState(parcelable, lastPageName, lastPagePosition, lastCalendarType)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        when (state) {
            is MonthState -> {
                super.onRestoreInstanceState(state.superState)
                lastPagePosition = state.lastPagePosition
                lastPageName = state.lastPageName
                lastCalendarType = state.lastCalendarType
                hasRestore = true
            }
            else -> super.onRestoreInstanceState(state)
        }
    }
}
