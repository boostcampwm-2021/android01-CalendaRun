package com.drunkenboys.ckscalendar

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.DayType
import com.drunkenboys.ckscalendar.databinding.ItemMonthCellBinding

class MonthAdapter : RecyclerView.Adapter<MonthAdapter.Holder>() {

    private val list = mutableListOf<CalendarDate>()

    fun setItems(list: List<CalendarDate>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val calculateHeight = parent.height / CALENDAR_COLUMN_SIZE

        return Holder(ItemMonthCellBinding.inflate(LayoutInflater.from(parent.context), parent, false), calculateHeight)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    class Holder(private val binding: ItemMonthCellBinding, calculateHeight: Int) : RecyclerView.ViewHolder(binding.root) {

        private val weekDayColor = Color.WHITE
        private val holidayColor = Color.RED
        private val saturdayColor = Color.BLUE

        init {
            binding.layoutMonthCell.layoutParams.height = calculateHeight
        }

        fun bind(item: CalendarDate) {
            binding.tvMonthDay.text = ""
            if (item.dayType != DayType.PADDING) {
                binding.tvMonthDay.text = item.date.dayOfMonth.toString()
            }
            val textColor = when (item.dayType) {
                DayType.HOLIDAY, DayType.SUNDAY -> holidayColor
                DayType.SATURDAY -> saturdayColor
                else -> weekDayColor
            }
            binding.tvMonthDay.setTextColor(textColor)
        }
    }

    companion object {
        private const val CALENDAR_COLUMN_SIZE = 7
    }
}
