package com.drunkenboys.calendarun.ui.addcalendar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.ui.addcalendar.CheckPointModel
import com.drunkenboys.calendarun.ui.base.BaseViewHolder

class AddCalendarRecyclerViewAdapter :
    ListAdapter<CheckPointModel, BaseViewHolder<ViewDataBinding>>(addCalenderRecyclerViewDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> {
        val layoutRes = when (viewType) {
            TYPE_FOOTER -> R.layout.view_check_point_footer
            else -> R.layout.view_check_point
        }

        return BaseViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                layoutRes,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewDataBinding>, position: Int) {
        // TODO bind 코드 짜기
    }

    override fun getItemViewType(position: Int): Int = when (position) {
        itemCount + 1 -> TYPE_FOOTER
        else -> TYPE_ITEM
    }

    companion object {
        private val addCalenderRecyclerViewDiffUtil = object : DiffUtil.ItemCallback<CheckPointModel>() {
            override fun areItemsTheSame(oldItem: CheckPointModel, newItem: CheckPointModel): Boolean {
                TODO("Not yet implemented")
            }

            override fun areContentsTheSame(oldItem: CheckPointModel, newItem: CheckPointModel): Boolean {
                TODO("Not yet implemented")
            }
        }

        private const val TYPE_ITEM = 1
        private const val TYPE_FOOTER = 2
    }
}
