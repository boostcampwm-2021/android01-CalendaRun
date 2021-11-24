package com.drunkenboys.ckscalendar.yearcalendar

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.drunkenboys.ckscalendar.data.CalendarDesignObject

@Composable
fun CustomTheme(
    design: CalendarDesignObject,
    content: @Composable () -> Unit
) {
    val lightColors = lightColors(
        primary = Color(design.weekDayTextColor),
        primaryVariant = Color(design.selectedFrameColor),
        secondary = Color(design.saturdayTextColor),
        secondaryVariant = Color(design.sundayTextColor),
        background = Color(design.backgroundColor),
    )

    MaterialTheme(
        colors = lightColors,
        content = content
    )
}