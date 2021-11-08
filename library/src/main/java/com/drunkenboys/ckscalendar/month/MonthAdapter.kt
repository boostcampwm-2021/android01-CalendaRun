package com.drunkenboys.ckscalendar.month

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.DayType
import com.drunkenboys.ckscalendar.databinding.ItemMonthCellBinding
import com.drunkenboys.ckscalendar.listener.OnDayClickListener
import com.drunkenboys.ckscalendar.listener.OnDaySecondClickListener

class MonthAdapter(val onDaySelect: (Int, Int) -> Unit) : ListAdapter<CalendarDate, MonthAdapter.Holder>(diffUtil) {

    var selectedPosition = -1
    var currentPagePosition = -1

    var onDateClickListener: OnDayClickListener? = null
    var onDateSecondClickListener: OnDaySecondClickListener? = null

    fun setItems(list: List<CalendarDate>, currentPagePosition: Int) {
        list.forEachIndexed { index, calendarDate ->
            if (calendarDate.isSelected) {
                selectedPosition = index
                return@forEachIndexed
            }
        }
        this.currentPagePosition = currentPagePosition
        submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val calculateHeight = parent.height / CALENDAR_COLUMN_SIZE

        return Holder(ItemMonthCellBinding.inflate(LayoutInflater.from(parent.context), parent, false), calculateHeight)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class Holder(private val binding: ItemMonthCellBinding, calculateHeight: Int) : RecyclerView.ViewHolder(binding.root) {

        private val weekDayColor = Color.BLACK
        private val holidayColor = Color.RED
        private val saturdayColor = Color.BLUE

        init {
            itemView.setOnClickListener {
                if (adapterPosition != -1 && currentList[adapterPosition].dayType != DayType.PADDING) {
                    notifyClickEventType()
                    notifyChangedSelectPosition(adapterPosition)
                    onDaySelect.invoke(currentPagePosition, selectedPosition)
                }
            }
            binding.layoutMonthCell.layoutParams.height = calculateHeight
        }

        fun bind(item: CalendarDate) {
            binding.layoutMonthCell.isSelected = item.isSelected

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

        private fun notifyChangedSelectPosition(position: Int) {
            val selectedTemp = selectedPosition
            selectedPosition = position

            if (selectedTemp != -1) {
                currentList[selectedTemp].isSelected = false
                notifyItemChanged(selectedTemp)
            }

            currentList[position].isSelected = true
            notifyItemChanged(position)
        }

        private fun notifyClickEventType() {
            if (selectedPosition == adapterPosition) {
                onDateSecondClickListener?.onDayClick(currentList[adapterPosition].date, adapterPosition)
            } else {
                onDateClickListener?.onDayClick(currentList[adapterPosition].date, adapterPosition)
            }
        }
    }

    companion object {

        private val diffUtil = object : DiffUtil.ItemCallback<CalendarDate>() {

            override fun areItemsTheSame(oldItem: CalendarDate, newItem: CalendarDate) = oldItem.date == newItem.date

            override fun areContentsTheSame(oldItem: CalendarDate, newItem: CalendarDate) = oldItem == newItem
        }

        private const val CALENDAR_COLUMN_SIZE = 7
    }
}
