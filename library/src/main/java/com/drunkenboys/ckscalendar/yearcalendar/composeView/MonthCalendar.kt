package com.drunkenboys.ckscalendar.yearcalendar.composeView

import android.view.Gravity
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import com.drunkenboys.ckscalendar.data.*
import com.drunkenboys.ckscalendar.utils.calendarSetToCalendarDatesList
import com.drunkenboys.ckscalendar.yearcalendar.YearCalendarViewModel
import java.time.LocalDate

@Composable
fun MonthCalendar(
    month: CalendarSet,
    listState: LazyListState,
    dayColumnModifier: (CalendarDate) -> Modifier,
    viewModel: YearCalendarViewModel
) {
    calendarSetToCalendarDatesList(month, viewModel.schedules.value).forEach { week ->
        WeekCalendar(
            viewModel = viewModel,
            listState = listState,
            month = month,
            dayColumnModifier = dayColumnModifier,
            week = week
        )
    }
}

@Preview
@Composable
fun PreviewMonth() {
    val month = CalendarSet(
        id = 0,
        name = "",
        startDate = LocalDate.now().withDayOfMonth(1),
        endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
    )
    val dayColumnModifier = { day: CalendarDate ->
        Modifier.layoutId(day.date.toString())
    }

    val viewModel = YearCalendarViewModel()
    viewModel.setDesign(CalendarDesignObject(
        textAlign = Gravity.END
    ))

    MonthCalendar(
        month = month,
        listState = rememberLazyListState(),
        dayColumnModifier = dayColumnModifier,
        viewModel = viewModel
    )
}