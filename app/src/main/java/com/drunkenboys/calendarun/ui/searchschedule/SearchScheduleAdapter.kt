package com.drunkenboys.calendarun.ui.searchschedule

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ListAdapter
import com.drunkenboys.calendarun.BR
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.ui.base.BaseViewHolder
import com.drunkenboys.calendarun.ui.searchschedule.model.DateItem
import com.drunkenboys.calendarun.ui.searchschedule.model.DateScheduleItem
import com.drunkenboys.calendarun.ui.searchschedule.model.ScheduleItem

class SearchScheduleAdapter : ListAdapter<DateScheduleItem, BaseViewHolder<ViewDataBinding>>(DateScheduleItem.diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> =
        BaseViewHolder(parent, viewType)

    override fun onBindViewHolder(holder: BaseViewHolder<ViewDataBinding>, position: Int) {
        holder.binding.setVariable(BR.item, currentList[position])
        holder.binding.executePendingBindings()
    }

    override fun getItemViewType(position: Int) = when (currentList[position]) {
        is DateItem -> R.layout.item_date
        is ScheduleItem -> R.layout.item_schedule
    }
}
