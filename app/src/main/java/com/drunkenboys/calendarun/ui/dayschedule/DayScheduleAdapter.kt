package com.drunkenboys.calendarun.ui.dayschedule

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.ItemDateScheduleBinding
import com.drunkenboys.calendarun.ui.base.BaseViewHolder
import com.drunkenboys.calendarun.ui.searchschedule.model.ScheduleItem

class DayScheduleAdapter : ListAdapter<ScheduleItem, BaseViewHolder<ItemDateScheduleBinding>>(ScheduleItem.diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ItemDateScheduleBinding> =
        BaseViewHolder(parent, R.layout.item_date_schedule)

    override fun onBindViewHolder(holder: BaseViewHolder<ItemDateScheduleBinding>, position: Int) {
        holder.binding.item = currentList[position]
        holder.binding.executePendingBindings()
    }
}
