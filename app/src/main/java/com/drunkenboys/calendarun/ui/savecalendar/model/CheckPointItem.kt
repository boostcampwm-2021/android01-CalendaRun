package com.drunkenboys.calendarun.ui.savecalendar.model

import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate

data class CheckPointItem(
    val id: Long = 0L,
    val name: MutableStateFlow<String> = MutableStateFlow(""),
    val startDate: MutableStateFlow<LocalDate?> = MutableStateFlow(null),
    val endDate: MutableStateFlow<LocalDate?> = MutableStateFlow(null),
    var check: Boolean = false
) : Comparable<CheckPointItem> {

    override fun compareTo(other: CheckPointItem): Int =
        compareValuesBy(this, other) { checkPoint -> checkPoint.startDate.value }


    companion object {
        val diffUtil by lazy {
            object : DiffUtil.ItemCallback<CheckPointItem>() {
                override fun areItemsTheSame(oldItem: CheckPointItem, newItem: CheckPointItem) =
                    oldItem.startDate == newItem.startDate

                override fun areContentsTheSame(oldItem: CheckPointItem, newItem: CheckPointItem) =
                    oldItem == newItem
            }
        }
    }
}
