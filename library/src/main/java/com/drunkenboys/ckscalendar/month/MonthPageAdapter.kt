package com.drunkenboys.ckscalendar.month

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drunkenboys.ckscalendar.data.*
import com.drunkenboys.ckscalendar.databinding.ItemMonthPageBinding
import com.drunkenboys.ckscalendar.listener.OnDayClickListener
import com.drunkenboys.ckscalendar.listener.OnDaySecondClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate


class MonthPageAdapter : RecyclerView.Adapter<MonthPageAdapter.Holder>() {

    private val list = mutableListOf<CalendarSet>()

    private val schedules = mutableListOf<CalendarScheduleObject>()

    private val cachedCalendar = HashMap<Int, List<CalendarDate>>()

    private lateinit var calendarDesign: CalendarDesignObject

    private var lastSelectPagePosition = -1
    private var lastSelectDayPosition = -1

    private val today = LocalDate.now()
    private var isFirstToday = true

    private val monthCellFactory = MonthCellFactory()

    var onDayClickListener: OnDayClickListener? = null
    var onDaySecondClickListener: OnDaySecondClickListener? = null

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
        holder.bind(list[position], onDayClickListener, onDaySecondClickListener)
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
