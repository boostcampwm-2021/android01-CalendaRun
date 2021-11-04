package com.drunkenboys.calendarun.ui.addcalendar.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.drunkenboys.calendarun.BR
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.ui.addcalendar.CheckPointModel
import com.drunkenboys.calendarun.ui.base.BaseViewHolder

class AddCalendarRecyclerViewAdapter :
    ListAdapter<CheckPointModel, BaseViewHolder<ViewDataBinding>>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> {
        return BaseViewHolder(parent, R.layout.view_check_point)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewDataBinding>, position: Int) {
        holder.binding.setVariable(BR.item, currentList[position])
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<CheckPointModel>() {
            override fun areItemsTheSame(oldItem: CheckPointModel, newItem: CheckPointModel) =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: CheckPointModel, newItem: CheckPointModel) =
                oldItem == newItem
        }
    }
}
