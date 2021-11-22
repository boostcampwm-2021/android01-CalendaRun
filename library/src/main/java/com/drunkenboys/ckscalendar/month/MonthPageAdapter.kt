package com.drunkenboys.ckscalendar.month

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drunkenboys.ckscalendar.data.*
import com.drunkenboys.ckscalendar.databinding.ItemMonthPageBinding
import com.drunkenboys.ckscalendar.listener.OnDayClickListener
import com.drunkenboys.ckscalendar.listener.OnDaySecondClickListener
import com.drunkenboys.ckscalendar.utils.context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate


class MonthPageAdapter : RecyclerView.Adapter<MonthPageAdapter.Holder>() {

    private val list = mutableListOf<CalendarSet>()

    private val schedules = mutableListOf<CalendarScheduleObject>()

    private val cachedCalendar = HashMap<Int, List<CalendarDate>>()
    private val infinityCalendar = HashMap<Int, CalendarSet>()

    private lateinit var calendarDesign: CalendarDesignObject

    private var lastSelectPagePosition = -1
    private var lastSelectDayPosition = -1

    private val today = LocalDate.now()
    private var isFirstToday = true

    private var isNormalCalendar = true

    private val monthCellFactory = MonthCellFactory()

    var onDayClickListener: OnDayClickListener? = null
    var onDaySecondClickListener: OnDaySecondClickListener? = null

    fun getCalendarSetName(position: Int): CalendarSet? {
        return if (isNormalCalendar) {
            infinityCalendar[position]
        } else {
            list[position]
        }
    }

    fun setItems(list: List<CalendarSet>, isNormalCalendar: Boolean) {
        cachedCalendar.clear()

        this.isNormalCalendar = isNormalCalendar
        if (isNormalCalendar) {
            list.forEachIndexed { index, calendarSet ->
                infinityCalendar[index + Int.MAX_VALUE / 2] = calendarSet
            }
        }

        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemMonthPageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val calendarSet = if (isNormalCalendar) {
            // hash key != position
            val preCalendarSet = infinityCalendar[position - 1]
            val nextCalendarSet = infinityCalendar[position + 1]
            //현재가 1월
            if (preCalendarSet == null) {
                val currentSet = (infinityCalendar[position]?.startDate?.year ?: today.year) - 1
                CalendarSet.generateCalendarOfYear(holder.context(), currentSet)
                    .forEachIndexed { index, calendarSet ->
                        infinityCalendar[position - 12 + index] = calendarSet
                    }
            }
            // 현재가 12월
            if (nextCalendarSet == null) {
                val currentSet = (infinityCalendar[position]?.startDate?.year ?: today.year) + 1
                CalendarSet.generateCalendarOfYear(holder.context(), currentSet)
                    .forEachIndexed { index, calendarSet ->
                        infinityCalendar[position + 1 + index] = calendarSet
                    }
            }
            infinityCalendar.get(position)

        } else {
            list[position]
        }
        holder.bind(calendarSet!!, onDayClickListener, onDaySecondClickListener)
    }

    override fun getItemCount(): Int = if (isNormalCalendar) Int.MAX_VALUE else list.size


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
        }

        fun bind(
            item: CalendarSet,
            onDayClick: OnDayClickListener?,
            onDaySecondClick: OnDaySecondClickListener?
        ) {
            binding.rvMonthCalendar.setBackgroundColor(calendarDesign.backgroundColor)
            CoroutineScope(Dispatchers.Default).launch {
                val dates = mutableListOf<CalendarDate>()

                cachedCalendar[item.id]?.let {
                    dates.addAll(it)
                } ?: run {
                    dates.addAll(monthCellFactory.makeCell(item))
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
                withContext(Dispatchers.Main) {
                    monthAdapter.setItems(dates, schedules, calendarDesign, adapterPosition)
                    monthAdapter.onDayClickListener = onDayClick
                    monthAdapter.onDaySecondClickListener = onDaySecondClick
                }
            }
        }
    }
}
