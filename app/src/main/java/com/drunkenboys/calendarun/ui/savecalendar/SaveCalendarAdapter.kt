package com.drunkenboys.calendarun.ui.savecalendar

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ListAdapter
import com.drunkenboys.calendarun.BR
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.ItemSliceBinding
import com.drunkenboys.calendarun.ui.base.BaseViewHolder
import com.drunkenboys.calendarun.ui.savecalendar.model.SliceItem
import com.drunkenboys.calendarun.view.ErrorGuideEditText
import com.drunkenboys.calendarun.view.ErrorGuideTextView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SaveCalendarAdapter(
    private val viewLifecycleOwner: LifecycleOwner,
    val onClick: (SliceItem) -> Unit
) : ListAdapter<SliceItem, BaseViewHolder<ItemSliceBinding>>(SliceItem.diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ItemSliceBinding> =
        BaseViewHolder(parent, R.layout.item_slice)

    override fun onBindViewHolder(holder: BaseViewHolder<ItemSliceBinding>, position: Int) {
        with(holder.binding) {
            setVariable(BR.item, currentList[position])
            setVariable(BR.adapter, this@SaveCalendarAdapter)
            lifecycleOwner = viewLifecycleOwner

            collectNameBlank(currentList[position], holder.binding.etSliceName)
            collectStartBlank(currentList[position], holder.binding.tvSliceStartDatePicker)
            collectEndBlank(currentList[position], holder.binding.tvSliceEndDatePicker)
        }
    }

    private fun collectNameBlank(item: SliceItem, view: ErrorGuideEditText) {
        viewLifecycleOwner.lifecycleScope.launch {
            item.isNameBlank.collectLatest {
                view.isError = true
            }

        }
    }

    private fun collectStartBlank(item: SliceItem, view: ErrorGuideTextView) {
        viewLifecycleOwner.lifecycleScope.launch {
            item.isStartDateBlank.collectLatest {
                view.isError = true
            }
        }
    }

    private fun collectEndBlank(item: SliceItem, view: ErrorGuideTextView) {
        viewLifecycleOwner.lifecycleScope.launch {
            item.isEndDateBlank.collectLatest {
                view.isError = true
            }
        }
    }
}
