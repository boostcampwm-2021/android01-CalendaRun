package com.drunkenboys.calendarun.ui.addcalendar.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.drunkenboys.calendarun.data.checkpoint.entity.CheckPoint
import com.drunkenboys.calendarun.databinding.ViewCheckPointBinding
import com.drunkenboys.calendarun.ui.base.BaseViewHolder

class AddCalendarRecyclerViewAdapter :
    ListAdapter<CheckPoint, BaseViewHolder<ViewDataBinding>>(diffUtil) {

    override fun getItemCount() = super.getItemCount() + 1

    override fun getItemViewType(position: Int) = when (position) {
        itemCount + 1 -> TYPE_FOOTER
        else -> TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> = BaseViewHolder(parent, viewType)

    override fun onBindViewHolder(holder: BaseViewHolder<ViewDataBinding>, position: Int) {
        if (getItemViewType(position) == TYPE_ITEM) {
            val item = getItem(position)
            (holder.binding as ViewCheckPointBinding).checkPoint = item
        }
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<CheckPoint>() {
            override fun areItemsTheSame(oldItem: CheckPoint, newItem: CheckPoint) =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: CheckPoint, newItem: CheckPoint) =
                oldItem == newItem
        }

        private const val TYPE_ITEM = 1
        private const val TYPE_FOOTER = 2
    }
}
