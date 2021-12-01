package com.drunkenboys.calendarun.ui.managecalendar

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.drunkenboys.calendarun.BR
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.ItemCalendarBinding
import com.drunkenboys.calendarun.ui.base.BaseViewHolder
import com.drunkenboys.calendarun.ui.managecalendar.model.CalendarItem

class ManageCalendarAdapter(val onClick: (CalendarItem) -> Unit) :
    ListAdapter<CalendarItem, BaseViewHolder<ItemCalendarBinding>>(CalendarItem.diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ItemCalendarBinding> =
        BaseViewHolder(parent, R.layout.item_calendar)

    override fun onBindViewHolder(holder: BaseViewHolder<ItemCalendarBinding>, position: Int) {
        with(holder.binding) {
            setVariable(BR.item, currentList[position])
            setVariable(BR.adapter, this@ManageCalendarAdapter)
        }
    }
}
