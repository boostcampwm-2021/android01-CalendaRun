package com.drunkenboys.ckscalendar.yearcalendar.composeView

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.DayType
import com.drunkenboys.ckscalendar.utils.GravityMapper
import com.drunkenboys.ckscalendar.utils.dp
import com.drunkenboys.ckscalendar.yearcalendar.CustomTheme
import com.drunkenboys.ckscalendar.yearcalendar.YearCalendarViewModel
import java.time.LocalDate

@Composable
fun DayText(
    day: CalendarDate,
    viewModel: YearCalendarViewModel,
    isFirstOfCalendarSet: Boolean
) {
    BaseText(
        text = if (isFirstOfCalendarSet) { "${day.date.monthValue}. "
        } else {
            ""
        } + "${day.date.dayOfMonth}",
        type = day.dayType,
        modifier = Modifier.border(
            width = 1.dp,
            shape = CircleShape,
            color = if (LocalDate.now() == day.date) MaterialTheme.colors.primary else Color.Transparent
        )
            .padding(5.dp),
        textAlign = GravityMapper.toTextAlign(viewModel.design.value.textAlign),
        fontWeight = if (isFirstOfCalendarSet) FontWeight.Bold else null,
        design =  viewModel.design.value
    )
}

@Preview
@Composable
fun PreviewDayText() {
    val viewModel = YearCalendarViewModel()
    CustomTheme(design = viewModel.design.value) {
        DayText(day = CalendarDate(date = LocalDate.now(), dayType = DayType.WEEKDAY), viewModel = viewModel, true)
    }
}