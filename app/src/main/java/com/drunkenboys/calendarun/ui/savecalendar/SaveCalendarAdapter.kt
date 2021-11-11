package com.drunkenboys.calendarun.ui.savecalendar

import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import com.drunkenboys.calendarun.BR
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.ItemCheckPointBinding
import com.drunkenboys.calendarun.ui.base.BaseViewHolder
import com.drunkenboys.calendarun.ui.savecalendar.model.CheckPointItem
import com.drunkenboys.calendarun.util.showDatePickerDialog

class SaveCalendarAdapter(private val viewLifecycleOwner: LifecycleOwner) :
    ListAdapter<CheckPointItem, BaseViewHolder<ItemCheckPointBinding>>(CheckPointItem.diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ItemCheckPointBinding> =
        BaseViewHolder(parent, R.layout.item_check_point)

    fun setCheckPointDate(context: Context, item: CheckPointItem) {
        showDatePickerDialog(context) { _, year, month, dayOfMonth ->
            val date = context.getString(R.string.ui_date_format, year, month + 1, dayOfMonth)
            item.date.value = date
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemCheckPointBinding>, position: Int) {
        with(holder.binding) {
            setVariable(BR.item, currentList[position])
            setVariable(BR.adapter, this@SaveCalendarAdapter)
            lifecycleOwner = viewLifecycleOwner
        }
    }
}
