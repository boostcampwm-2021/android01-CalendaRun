package com.drunkenboys.ckscalendar.month

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.CalendarSet
import com.drunkenboys.ckscalendar.data.DayType
import com.drunkenboys.ckscalendar.databinding.ItemMonthPageBinding
import com.drunkenboys.ckscalendar.utils.TimeUtils.parseDayWeekToDayType
import java.time.DayOfWeek
import java.time.LocalDate


class MonthPageAdapter : RecyclerView.Adapter<MonthPageAdapter.Holder>() {

    private val list = mutableListOf<CalendarSet>()

    fun setItems(list: List<CalendarSet>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemMonthPageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    class Holder(private val binding: ItemMonthPageBinding) : RecyclerView.ViewHolder(binding.root) {

        private val monthAdapter = MonthAdapter()

        init {
            binding.rvMonthCalendar.adapter = monthAdapter
            binding.rvMonthCalendar.layoutManager = GridLayoutManager(itemView.context, 7)
        }

        fun bind(item: CalendarSet) {
            val dates = mutableListOf<CalendarDate>()
            val startMonth = item.startDate.monthValue
            val startDay = item.startDate.dayOfWeek
            val endMonth = item.endDate.monthValue

            (startMonth..endMonth).forEach { month ->
                when (month) {
                    startMonth -> {
                        // add Start Padding
                        if (startDay != DayOfWeek.SUNDAY) {
                            dates.addAll(makePadding(startDay.ordinal))
                        }

                        // add Start Dates
                        dates.addAll(makeDates(item.startDate, month))
                    }
                    else -> {
                        // add Normal Dates
                        dates.addAll(makeDates(item.endDate, month))
                    }
                }
            }
            // add End Padding
            val weekPadding = 6 - dates.size % WEEK_SIZE
            dates.addAll(makePadding(weekPadding))

            // add FullSize Padding
            if (dates.size < FULL_SIZE_CALENDAR) {
                val fullSizePadding = FULL_SIZE_CALENDAR - dates.size - 1
                dates.addAll(makePadding(fullSizePadding))
            }

            monthAdapter.setItems(dates)
        }

        private fun makeDates(date: LocalDate, month: Int): List<CalendarDate> {
            return (1..date.lengthOfMonth()).map { day ->
                val local = LocalDate.of(date.year, month, day)
                val dayType = parseDayWeekToDayType(local.dayOfWeek)
                CalendarDate(local, dayType)
            }
        }

        private fun makePadding(paddingCount: Int): List<CalendarDate> {
            return (0..paddingCount).map {
                CalendarDate(LocalDate.now(), DayType.PADDING)
            }
        }

        companion object {
            private const val WEEK_SIZE = 7
            private const val FULL_SIZE_CALENDAR = 42
        }
    }
}
