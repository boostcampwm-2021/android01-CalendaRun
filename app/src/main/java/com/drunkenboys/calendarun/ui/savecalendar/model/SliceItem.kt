package com.drunkenboys.calendarun.ui.savecalendar.model

import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate

data class SliceItem(
    val id: Long = 0L,
    val name: MutableStateFlow<String> = MutableStateFlow(""),
    val startDate: MutableStateFlow<LocalDate?> = MutableStateFlow(null),
    val endDate: MutableStateFlow<LocalDate?> = MutableStateFlow(null),
    var check: Boolean = false,
    val isNameBlank: MutableSharedFlow<Unit> = MutableSharedFlow(),
    val isStartDateBlank: MutableSharedFlow<Unit> = MutableSharedFlow(),
    val isEndDateBlank: MutableSharedFlow<Unit> = MutableSharedFlow()
) : Comparable<SliceItem> {

    override fun compareTo(other: SliceItem): Int =
        compareValuesBy(this, other) { slice -> slice.startDate.value }

    companion object {

        val diffUtil by lazy {
            object : DiffUtil.ItemCallback<SliceItem>() {
                override fun areItemsTheSame(oldItem: SliceItem, newItem: SliceItem) =
                    oldItem.startDate == newItem.startDate

                override fun areContentsTheSame(oldItem: SliceItem, newItem: SliceItem) =
                    oldItem == newItem
            }
        }
    }
}
