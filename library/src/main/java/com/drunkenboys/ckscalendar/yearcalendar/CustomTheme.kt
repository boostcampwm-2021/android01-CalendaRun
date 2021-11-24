package com.drunkenboys.ckscalendar.yearcalendar

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.drunkenboys.ckscalendar.R
import com.drunkenboys.ckscalendar.data.CalendarDesignObject

@Composable
fun CustomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    design: CalendarDesignObject,
    content: @Composable () -> Unit
) {
    val darkColors = darkColors(
        primary = colorResource(id = R.color.calendar_white),
        primaryVariant = Color(design.selectedFrameColor),
        secondary = Color(design.saturdayTextColor),
        secondaryVariant = Color(design.sundayTextColor),
        background = colorResource(id = R.color.calendar_black),
    )

    val lightColors = lightColors(
        primary = Color(design.weekDayTextColor),
        primaryVariant = Color(design.selectedFrameColor),
        secondary = Color(design.saturdayTextColor),
        secondaryVariant = Color(design.sundayTextColor),
        background = Color(design.backgroundColor),
    )

    MaterialTheme(
        colors = if (darkTheme && design == CalendarDesignObject.getDefaultDesign()) darkColors else lightColors,
        content = content
    )
}