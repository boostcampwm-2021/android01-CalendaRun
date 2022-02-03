package com.drunkenboys.ckscalendar.yearcalendar

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.drunkenboys.ckscalendar.data.CalendarDesignObject

/**
 * FIXME: View에서 Theme과 design의 color를 혼용하고 있음.
 * Theme
 */
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

    // 존재하는 MaterialTheme에서 color set을 커스텀.
    // View를 선언할 때 MaterialTheme로 감싸면 테마를 적용할 수 있다.
    MaterialTheme(
        colors = lightColors,
        content = content
    )
}