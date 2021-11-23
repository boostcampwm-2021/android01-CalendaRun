package com.drunkenboys.ckscalendar.yearcalendar.composeView

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.CalendarDesignObject
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
    val color = when (day.dayType) { // FIXME: month 와 통합
        DayType.HOLIDAY -> Color(viewModel.design.value.holidayTextColor)
        DayType.SATURDAY -> Color(viewModel.design.value.saturdayTextColor)
        DayType.SUNDAY -> Color(viewModel.design.value.sundayTextColor)
        else -> MaterialTheme.colors.primary
    }

    Text(
        text = "${day.date.dayOfMonth}",
        color = color,
        modifier = Modifier.layoutId(day.date.toString()),
        textAlign = GravityMapper.toTextAlign(viewModel.design.value.textAlign),
        fontSize = viewModel.design.value.textSize.dp()
    )
}

@Preview
@Composable
fun PreviewDayText() {
    val viewModel = YearCalendarViewModel()
    CustomTheme(design = viewModel.design.value) {
        DayText(day = CalendarDate(date = LocalDate.now(), dayType = DayType.WEEKDAY), viewModel = viewModel)
    }
}