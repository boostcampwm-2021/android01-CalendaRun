package com.drunkenboys.calendarun.ui.searchschedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.drunkenboys.calendarun.BR
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.ItemDateBinding
import com.drunkenboys.calendarun.databinding.ItemDateScheduleBinding
import com.drunkenboys.calendarun.ui.base.BaseViewHolder
import com.drunkenboys.calendarun.ui.searchschedule.model.DateItem

class SearchScheduleAdapter : androidx.recyclerview.widget.ListAdapter<DateItem, BaseViewHolder<ItemDateBinding>>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ItemDateBinding> =
        BaseViewHolder(parent, R.layout.item_date)

    override fun onBindViewHolder(holder: BaseViewHolder<ItemDateBinding>, position: Int) {
        holder.binding.setVariable(BR.item, currentList[position])
        addDateScheduleItem(holder.binding, currentList[position])
        holder.binding.executePendingBindings()
    }

    private fun addDateScheduleItem(binding: ItemDateBinding, dateItem: DateItem) = with(binding) {
        layoutDateSchedule.removeAllViews()
        dateItem.scheduleList.forEach { schedule ->
            val dateScheduleBinding = ItemDateScheduleBinding.inflate(LayoutInflater.from(root.context), layoutDateSchedule, false)
            dateScheduleBinding.item = schedule
            layoutDateSchedule.addView(dateScheduleBinding.root)
        }
    }

    companion object {

        private val diffUtil = object : DiffUtil.ItemCallback<DateItem>() {

            override fun areItemsTheSame(oldItem: DateItem, newItem: DateItem) = oldItem.hashCode() == newItem.hashCode()

            override fun areContentsTheSame(oldItem: DateItem, newItem: DateItem) = oldItem == newItem
        }
    }
}
