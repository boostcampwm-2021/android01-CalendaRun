package com.drunkenboys.calendarun.ui.savecalendar.model

import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.flow.MutableStateFlow

data class CheckPointItem(
    val name: MutableStateFlow<String> = MutableStateFlow(""),
    val date: MutableStateFlow<String> = MutableStateFlow(""),
    var check: Boolean = false
) {

    companion object {

        val diffUtil by lazy {
            object : DiffUtil.ItemCallback<CheckPointItem>() {
                override fun areItemsTheSame(oldItem: CheckPointItem, newItem: CheckPointItem) =
                    oldItem.date == newItem.date

                override fun areContentsTheSame(oldItem: CheckPointItem, newItem: CheckPointItem) =
                    oldItem == newItem
            }
        }
    }
}
