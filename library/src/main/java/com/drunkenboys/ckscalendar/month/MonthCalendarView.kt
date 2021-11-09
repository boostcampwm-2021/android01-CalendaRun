package com.drunkenboys.ckscalendar.month

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.drunkenboys.ckscalendar.R
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import com.drunkenboys.ckscalendar.data.CalendarSet
import com.drunkenboys.ckscalendar.databinding.LayoutMonthCalendarBinding
import com.drunkenboys.ckscalendar.listener.OnDayClickListener
import com.drunkenboys.ckscalendar.listener.OnDaySecondClickListener
import java.time.LocalDate

class MonthCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutMonthCalendarBinding by lazy { LayoutMonthCalendarBinding.inflate(LayoutInflater.from(context), this, true) }

    private val pageAdapter = MonthPageAdapter()

    private var calendarList = listOf<CalendarSet>()

    init {
        binding.vpMonthPage.adapter = pageAdapter
        val today = LocalDate.now()
        calendarList = generateCalendarOfYear(today.year)

        pageAdapter.setItems(calendarList)

        binding.vpMonthPage.offscreenPageLimit =3
        binding.vpMonthPage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tvMonthCalendarViewCurrentMonth.text = calendarList[position].name
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })

        calendarList.forEachIndexed { index, calendarSet ->
            if (calendarSet.startDate.monthValue == today.monthValue) {
                binding.vpMonthPage.setCurrentItem(index, false)
                return@forEachIndexed
            }
        }
    }

    fun setOnDateClickListener(onDateClickListener: OnDayClickListener) {
        pageAdapter.onDateClickListener = onDateClickListener
    }

    fun setOnDaySecondClickListener(onDateSecondClickListener: OnDaySecondClickListener) {
        pageAdapter.onDateSecondClickListener = onDateSecondClickListener
    }

    fun setSchedules(schedules: List<CalendarScheduleObject>) {
        pageAdapter.setSchedule(schedules.sortedBy { it.startDate })
    }

    private fun generateCalendarOfYear(year: Int): List<CalendarSet> {
        return (1..12).map { month ->
            val start = LocalDate.of(year, month, 1)
            val end = LocalDate.of(year, month, start.lengthOfMonth())
            CalendarSet(
                id = month,
                name = "${month}${context.getString(R.string.common_month)}",
                startDate = start,
                endDate = end
            )
        }
    }
}
