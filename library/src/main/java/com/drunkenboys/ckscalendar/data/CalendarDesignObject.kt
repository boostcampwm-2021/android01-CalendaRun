package com.drunkenboys.ckscalendar.data

import android.graphics.Color
import android.view.Gravity
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.drunkenboys.ckscalendar.R

/**
 * CalendarView의 디자인을 위한 설정
 */
data class CalendarDesignObject(
    @ColorInt var weekDayTextColor: Int = Color.BLACK,
    @ColorInt var holidayTextColor: Int = ScheduleColorType.RED.color,
    @ColorInt var saturdayTextColor: Int = ScheduleColorType.BLUE.color,
    @ColorInt var sundayTextColor: Int = ScheduleColorType.RED.color,
    @ColorInt var selectedFrameColor: Int = ScheduleColorType.GRAY.color,
    @ColorInt var backgroundColor: Int = Color.WHITE,
    @DrawableRes var selectedFrameDrawable: Int = R.drawable.bg_month_date_selected,
    var textSize: Int = 10,
    var textAlign: Int = Gravity.CENTER_VERTICAL,
    var weekSimpleStringSet: List<String> = listOf(),
    var weekFullStringSet: List<String> = listOf(),
    var visibleScheduleCount: Int = 3,
) {
    companion object {

        fun getDefaultDesign() = CalendarDesignObject()

        fun getDarkDesign() = getDefaultDesign().apply {
            backgroundColor = Color.BLACK
            weekDayTextColor = Color.WHITE
            selectedFrameColor = Color.WHITE
        }
    }
}
