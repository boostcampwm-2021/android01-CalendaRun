package com.drunkenboys.ckscalendar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.drunkenboys.ckscalendar.data.CalendarSet
import com.drunkenboys.ckscalendar.databinding.LayoutMonthCalendarBinding
import com.drunkenboys.ckscalendar.month.MonthPageAdapter
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

        binding.vpMonthPage.offscreenPageLimit = calendarList.size
        binding.vpMonthPage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tvMonthCurrentMonth.text = calendarList[position].name
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
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
