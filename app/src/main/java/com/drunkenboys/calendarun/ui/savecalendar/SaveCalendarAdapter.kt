package com.drunkenboys.calendarun.ui.savecalendar

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ListAdapter
import com.drunkenboys.calendarun.BR
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.ItemCheckPointBinding
import com.drunkenboys.calendarun.ui.base.BaseViewHolder
import com.drunkenboys.calendarun.ui.savecalendar.model.CheckPointItem
import com.drunkenboys.calendarun.ui.saveschedule.model.DateType
import com.drunkenboys.calendarun.view.ErrorGuideEditText
import com.drunkenboys.calendarun.view.ErrorGuideTextView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SaveCalendarAdapter(
    private val viewLifecycleOwner: LifecycleOwner,
    val onClick: (CheckPointItem, DateType) -> Unit
) : ListAdapter<CheckPointItem, BaseViewHolder<ItemCheckPointBinding>>(CheckPointItem.diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ItemCheckPointBinding> =
        BaseViewHolder(parent, R.layout.item_check_point)

    override fun onBindViewHolder(holder: BaseViewHolder<ItemCheckPointBinding>, position: Int) {
        with(holder.binding) {
            setVariable(BR.item, currentList[position])
            setVariable(BR.adapter, this@SaveCalendarAdapter)
            lifecycleOwner = viewLifecycleOwner

            collectNameBlank(currentList[position], holder.binding.etCheckPointName)
            collectStartBlank(currentList[position], holder.binding.tvCheckPointStartDatePicker)
            collectEndBlank(currentList[position], holder.binding.tvCheckPointEndDatePicker)
        }
    }

    private fun collectNameBlank(item: CheckPointItem, view: ErrorGuideEditText) {
        viewLifecycleOwner.lifecycleScope.launch {
            item.isNameBlank.collectLatest {
                view.isError = true
            }

        }
    }

    private fun collectStartBlank(item: CheckPointItem, view: ErrorGuideTextView) {
        viewLifecycleOwner.lifecycleScope.launch {
            item.isStartDateBlank.collectLatest {
                view.isError = true
            }
        }
    }

    private fun collectEndBlank(item: CheckPointItem, view: ErrorGuideTextView) {
        viewLifecycleOwner.lifecycleScope.launch {
            item.isEndDateBlank.collectLatest {
                view.isError = true
            }
        }
    }
}
