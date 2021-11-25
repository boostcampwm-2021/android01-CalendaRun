package com.drunkenboys.calendarun.ui.dayschedule

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.ItemScheduleBinding
import com.drunkenboys.calendarun.ui.base.BaseViewHolder
import com.drunkenboys.calendarun.ui.searchschedule.model.ScheduleItem
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class DayScheduleAdapter(private val localDate: LocalDate) :
    ListAdapter<ScheduleItem, BaseViewHolder<ItemScheduleBinding>>(ScheduleItem.diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ItemScheduleBinding> =
        BaseViewHolder(parent, R.layout.item_schedule)

    override fun onBindViewHolder(holder: BaseViewHolder<ItemScheduleBinding>, position: Int) {
        holder.binding.item = currentList[position]
        holder.binding.time = LocalDateTime.of(localDate, LocalTime.MIN)
        holder.binding.executePendingBindings()
    }
}
