package com.drunkenboys.ckscalendar.monthcalendar

import android.os.Parcelable
import android.view.View
import kotlinx.parcelize.Parcelize

@Parcelize
class MonthState(
    val parcelable: Parcelable,
    val lastPageName: String = "",
    val lastPagePosition: Int = -1,
    val lastCalendarType: CalendarType = CalendarType.NONE
) : View.BaseSavedState(parcelable) {

    enum class CalendarType {
        DEFAULT, CUSTOM, NONE
    }
}
