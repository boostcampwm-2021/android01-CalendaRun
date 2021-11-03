package com.drunkenboys.ckscalendar.yearcalendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.databinding.ItemYearHeaderBinding
import com.drunkenboys.ckscalendar.databinding.ItemYearPageBinding

class YearPageAdapter : ListAdapter<CalendarDate, YearPageViewHolder>(DateDiffUtilCallback()) {

    private val calendar = mutableListOf<YearViewType>()

    private var monthCount = 1

    fun setItems(month: List<List<CalendarDate>>) {
        calendar.add(YearViewType.MonthEntity(monthCount++))
        month.forEach { week -> calendar.add(YearViewType.WeekEntity(week)) }

        notifyItemChanged(calendar.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YearPageViewHolder = when(viewType) {
        MONTH_TITLE_TYPE -> YearPageViewHolder.HeaderViewHolder(ItemYearHeaderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
        else -> YearPageViewHolder.PageViewHolder(ItemYearPageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: YearPageViewHolder, position: Int) {
        when(holder) {
            is YearPageViewHolder.HeaderViewHolder ->
                holder.bind((calendar[position] as YearViewType.MonthEntity).month)
            is YearPageViewHolder.PageViewHolder ->
                holder.bind((calendar[position] as YearViewType.WeekEntity).week)
        }
    }

    override fun getItemCount() = calendar.size

    override fun getItemViewType(position: Int) = when (calendar[position]) {
        is YearViewType.MonthEntity -> MONTH_TITLE_TYPE
        is YearViewType.WeekEntity -> WEEK_TYPE
    }

    class DateDiffUtilCallback : DiffUtil.ItemCallback<CalendarDate>() {

        override fun areItemsTheSame(oldItem: CalendarDate, newItem: CalendarDate) = oldItem == newItem

        override fun areContentsTheSame(oldItem: CalendarDate, newItem: CalendarDate) = oldItem.date == newItem.date
    }

    companion object {
        const val MONTH_TITLE_TYPE = 0
        const val WEEK_TYPE = 1
    }
}