package com.drunkenboys.ckscalendar.yearcalendar

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.drunkenboys.ckscalendar.data.CalendarDesignObject

@Composable
fun CustomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    design: CalendarDesignObject,
    content: @Composable () -> Unit
) {
    val darkColors = darkColors(
        primary = Color.White,
        primaryVariant = Color(design.selectedFrameColor),
        secondary = Color(design.saturdayTextColor),
        secondaryVariant = Color(design.sundayTextColor),
        background = Color.Black,
    )

    val lightColors = lightColors(
        primary = Color(design.weekDayTextColor),
        primaryVariant = Color(design.selectedFrameColor),
        secondary = Color(design.saturdayTextColor),
        secondaryVariant = Color(design.sundayTextColor),
        background = Color(design.backgroundColor),
    )

    MaterialTheme(
        colors = if (darkTheme) darkColors else lightColors,
        content = content
    )
}