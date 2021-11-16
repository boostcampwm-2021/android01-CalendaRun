package com.drunkenboys.ckscalendar.yearcalendar.composeView

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextAlign
import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.CalendarDesignObject
import com.drunkenboys.ckscalendar.data.DayType
import com.drunkenboys.ckscalendar.utils.dp

@Composable
fun DayText(day: CalendarDate, design: CalendarDesignObject) {
    val color = when (day.dayType) { // FIXME: month 와 통합
        DayType.HOLIDAY -> Color(design.holidayTextColor)
        DayType.SATURDAY -> Color(design.saturdayTextColor)
        DayType.SUNDAY -> Color(design.sundayTextColor)
        else -> MaterialTheme.colors.primary
    }

    // FIXME: mapper 추가
    val align = when (design.textAlign) {
        -1 -> TextAlign.Start
        0 -> TextAlign.Center
        1 -> TextAlign.End
        else -> TextAlign.Center
    }

    Text(
        text = "${day.date.dayOfMonth}",
        color = color,
        modifier = Modifier.layoutId(day.date.toString()),
        textAlign = align,
        fontSize = design.textSize.dp()
    )
}