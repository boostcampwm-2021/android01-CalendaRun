package com.drunkenboys.ckscalendar.month

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.DayType
import com.drunkenboys.ckscalendar.databinding.ItemMonthCellBinding
import com.drunkenboys.ckscalendar.listener.OnDayClickListener
import com.drunkenboys.ckscalendar.listener.OnDaySecondClickListener

class MonthAdapter(val onDaySelect: (Int, Int) -> Unit) : RecyclerView.Adapter<MonthAdapter.Holder>() {

    private val list = mutableListOf<CalendarDate>()

    var selectedPosition = -1
    var currentPagePosition = -1

    var onDateClickListener: OnDayClickListener? = null
    var onDateSecondClickListener: OnDaySecondClickListener? = null

    fun setItems(list: List<CalendarDate>, currentPagePosition: Int) {
        this.currentPagePosition = currentPagePosition
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

    inner class Holder(private val binding: ItemMonthCellBinding, calculateHeight: Int) : RecyclerView.ViewHolder(binding.root) {

        private val weekDayColor = Color.BLACK
        private val holidayColor = Color.RED
        private val saturdayColor = Color.BLUE

        init {
            itemView.setOnClickListener {
                if (adapterPosition != -1 && list[adapterPosition].dayType != DayType.PADDING) {
                    notifyClickEventType()
                    notifyChangedSelectPosition()
                    onDaySelect.invoke(currentPagePosition, selectedPosition)
                }
            }
            binding.layoutMonthCell.layoutParams.height = calculateHeight
        }

        fun bind(item: CalendarDate) {
            binding.layoutMonthCell.isSelected = adapterPosition == selectedPosition

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

        private fun notifyChangedSelectPosition() {
            val selectedTemp = selectedPosition
            selectedPosition = adapterPosition
            notifyItemChanged(selectedPosition)
            notifyItemChanged(selectedTemp)
        }

        private fun notifyClickEventType() {
            if (selectedPosition == adapterPosition) {
                onDateSecondClickListener?.onDayClick(list[adapterPosition].date, adapterPosition)
            } else {
                onDateClickListener?.onDayClick(list[adapterPosition].date, adapterPosition)
            }
        }
    }

    companion object {

        private const val CALENDAR_COLUMN_SIZE = 7
    }
}
