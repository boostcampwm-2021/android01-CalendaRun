package com.drunkenboys.calendarun.ui.dayschedule

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.ItemDateScheduleBinding
import com.drunkenboys.calendarun.ui.base.BaseViewHolder
import com.drunkenboys.calendarun.ui.searchschedule.model.DateScheduleItem

class DayScheduleAdapter : ListAdapter<DateScheduleItem, BaseViewHolder<ItemDateScheduleBinding>>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ItemDateScheduleBinding> =
        BaseViewHolder(parent, R.layout.item_date_schedule)

    override fun onBindViewHolder(holder: BaseViewHolder<ItemDateScheduleBinding>, position: Int) {
        holder.binding.item = currentList[position]
        holder.binding.executePendingBindings()
    }

    companion object {

        private val diffUtil = object : DiffUtil.ItemCallback<DateScheduleItem>() {
            override fun areItemsTheSame(oldItem: DateScheduleItem, newItem: DateScheduleItem) =
                oldItem.schedule.id == newItem.schedule.id

            override fun areContentsTheSame(oldItem: DateScheduleItem, newItem: DateScheduleItem) = oldItem == newItem
        }
    }
}
