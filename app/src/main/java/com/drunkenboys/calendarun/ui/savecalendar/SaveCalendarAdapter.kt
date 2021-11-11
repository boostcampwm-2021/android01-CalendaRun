package com.drunkenboys.calendarun.ui.savecalendar

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import com.drunkenboys.calendarun.BR
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.ItemCheckPointBinding
import com.drunkenboys.calendarun.ui.base.BaseViewHolder
import com.drunkenboys.calendarun.ui.savecalendar.model.CheckPointItem

class SaveCalendarAdapter(
    private val viewLifecycleOwner: LifecycleOwner,
    val onClick: (CheckPointItem) -> Unit
) : ListAdapter<CheckPointItem, BaseViewHolder<ItemCheckPointBinding>>(CheckPointItem.diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ItemCheckPointBinding> =
        BaseViewHolder(parent, R.layout.item_check_point)

    override fun onBindViewHolder(holder: BaseViewHolder<ItemCheckPointBinding>, position: Int) {
        with(holder.binding) {
            setVariable(BR.item, currentList[position])
            setVariable(BR.adapter, this@SaveCalendarAdapter)
            lifecycleOwner = viewLifecycleOwner
        }
    }
}
