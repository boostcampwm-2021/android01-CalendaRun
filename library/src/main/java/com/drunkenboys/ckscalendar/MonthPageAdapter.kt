package com.drunkenboys.ckscalendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.CalendarSet
import com.drunkenboys.ckscalendar.data.DayType
import com.drunkenboys.ckscalendar.databinding.ItemMonthPageBinding
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

        fun parseDayWeekToDayType(week: DayOfWeek): DayType {
            return when (week) {
                DayOfWeek.SATURDAY -> DayType.SATURDAY
                DayOfWeek.SUNDAY -> DayType.HOLIDAY
                else -> DayType.WEEKDAY
            }

        }

        fun bind(item: CalendarSet) {
            val dates = mutableListOf<CalendarDate>()
            val startMonth = item.startDate.monthValue
            val startDay = item.startDate.dayOfWeek
            val endMonth = item.endDate.monthValue
            val endDay = item.endDate.dayOfWeek

            (startMonth..endMonth).forEach { month ->
                when (month) {
                    startMonth -> {
                        (0..startDay.ordinal).forEach {
                            dates.add(CalendarDate(LocalDate.now(), DayType.PADDING))
                        }
                        (1..item.startDate.lengthOfMonth()).forEach { day ->
                            val local = LocalDate.of(item.startDate.year, month, day)
                            val dayType = parseDayWeekToDayType(local.dayOfWeek)
                            dates.add(CalendarDate(local, dayType))
                        }
                    }
                    endMonth -> {
                        (1..item.endDate.lengthOfMonth()).forEach { day ->
                            val local = LocalDate.of(item.startDate.year, month, day)
                            val dayType = parseDayWeekToDayType(local.dayOfWeek)
                            dates.add(CalendarDate(local, dayType))
                        }
                        (0..(7 - endDay.ordinal)).forEach {
                            dates.add(CalendarDate(LocalDate.now(), DayType.PADDING))
                        }
                    }
                    else -> {
                        (1..item.endDate.lengthOfMonth()).forEach { day ->
                            val local = LocalDate.of(item.startDate.year, month, day)
                            val dayType = parseDayWeekToDayType(local.dayOfWeek)
                            dates.add(CalendarDate(local, dayType))
                        }
                    }
                }
            }

            monthAdapter.setItems(dates)
        }
    }
}
