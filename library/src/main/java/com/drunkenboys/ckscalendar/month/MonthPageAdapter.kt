package com.drunkenboys.ckscalendar.month

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drunkenboys.ckscalendar.data.*
import com.drunkenboys.ckscalendar.databinding.ItemMonthPageBinding
import com.drunkenboys.ckscalendar.listener.OnDayClickListener
import com.drunkenboys.ckscalendar.listener.OnDaySecondClickListener
import com.drunkenboys.ckscalendar.utils.TimeUtils.parseDayWeekToDayType
import kotlinx.coroutines.*
import java.time.DayOfWeek
import java.time.LocalDate


class MonthPageAdapter : RecyclerView.Adapter<MonthPageAdapter.Holder>() {

    private val list = mutableListOf<CalendarSet>()

    private val schedules = mutableListOf<CalendarScheduleObject>()

    private val cachedCalendar = HashMap<Int, List<CalendarDate>>()

    private var calendarDesign = CalendarDesignObject.getDefaultDesign()

    private var lastSelectPagePosition = -1
    private var lastSelectDayPosition = -1

    private val today = LocalDate.now()
    private var isFirstToday = true

    var onDateClickListener: OnDayClickListener? = null
    var onDateSecondClickListener: OnDaySecondClickListener? = null

    fun setItems(list: List<CalendarSet>) {
        cachedCalendar.clear()
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemMonthPageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position], onDateClickListener, onDateSecondClickListener)
    }

    override fun getItemCount(): Int = list.size

    fun setSchedule(schedules: List<CalendarScheduleObject>) {
        this.schedules.clear()
        this.schedules.addAll(schedules)
        notifyDataSetChanged()
    }

    fun setDesign(calendarDesign: CalendarDesignObject) {
        this.calendarDesign = calendarDesign
        notifyDataSetChanged()
    }

    inner class Holder(private val binding: ItemMonthPageBinding) : RecyclerView.ViewHolder(binding.root) {

        private val weekSize = 7
        private val calendarFullSize = 42

        private val monthAdapter = MonthAdapter { pagePosition, selectPosition ->
            // TODO : 성능 문제로 개선 필요
            if (lastSelectPagePosition != pagePosition) {
                val preSelectedPagePosition = lastSelectPagePosition
                lastSelectPagePosition = pagePosition
                lastSelectDayPosition = selectPosition
                notifyItemChanged(preSelectedPagePosition)
            }
        }

        init {
            binding.rvMonthCalendar.adapter = monthAdapter
            binding.rvMonthCalendar.itemAnimator = null
            binding.rvMonthCalendar.layoutManager = GridLayoutManager(itemView.context, 7)
            binding.rvMonthCalendar.setBackgroundColor(calendarDesign.backgroundColor)
        }

        fun bind(
            item: CalendarSet,
            onDayClick: OnDayClickListener?,
            onDaySecondClick: OnDaySecondClickListener?
        ) {
            CoroutineScope(Dispatchers.Default).launch {
                val dates = mutableListOf<CalendarDate>()
                val startMonth = item.startDate.monthValue
                val startDay = item.startDate.dayOfWeek
                val endMonth = item.endDate.monthValue

                cachedCalendar[item.id]?.let {
                    dates.addAll(it)
                } ?: run {
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
                    val weekPadding = 6 - dates.size % weekSize
                    dates.addAll(makePadding(weekPadding))

                    // add FullSize Padding
                    if (dates.size < calendarFullSize) {
                        val fullSizePadding = calendarFullSize - dates.size - 1
                        dates.addAll(makePadding(fullSizePadding))
                    }
                }

                // check and set recycle data
                if (lastSelectPagePosition == adapterPosition) {
                    monthAdapter.selectedPosition = lastSelectDayPosition
                } else {
                    monthAdapter.selectedPosition = -1
                }

                if (isFirstToday) {
                    dates.find {
                        it.date.monthValue == today.monthValue &&
                                it.date.dayOfMonth == today.dayOfMonth &&
                                it.dayType != DayType.PADDING
                    }?.let {
                        it.isSelected = true
                        isFirstToday = false
                    }
                }
                withContext(Dispatchers.Main){
                    monthAdapter.setItems(dates, schedules, calendarDesign, adapterPosition)
                    monthAdapter.onDateClickListener = onDayClick
                    monthAdapter.onDateSecondClickListener = onDaySecondClick
                }
            }
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
    }
}
